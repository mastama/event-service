package com.yolifay.eventservice.service;

import com.yolifay.eventservice.client.IdentityClientFacade;
import com.yolifay.eventservice.dto.*;
import com.yolifay.eventservice.dto.pagination.BasePaging;
import com.yolifay.eventservice.dto.pagination.PageEnvelope;
import com.yolifay.eventservice.dto.pagination.SortMeta;
import com.yolifay.eventservice.entity.Event;
import com.yolifay.eventservice.entity.EventParticipant;
import com.yolifay.eventservice.exception.ConflictException;
import com.yolifay.eventservice.exception.DataNotFoundException;
import com.yolifay.eventservice.repository.EventParticipantRepository;
import com.yolifay.eventservice.repository.EventRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepo;
    private final EventParticipantRepository participantRepo;
    private final IdentityClientFacade identityClientFacade;

    private static final String START_TIME = "startTime";
    private static final String TITLE = "title";
    private static final String LOCATION = "location";

    // ----------------- Event ----------------
    @Transactional
    public EventResponse createEvent(EventCreateRequest req) {
        log.info("Start create event {}", req.title());

        if (req.endTime().isBefore(req.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        Event e = Event.builder()
                .title(req.title())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .location(req.location())
                .quota(req.quota())
                .description(req.description())
                .build();
        Event savedEvent = eventRepo.save(e);

        log.info("End create event {}", savedEvent.getTitle());
        return mapEventResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId) {
        log.info("Start get event by id {}", eventId);

        Event e = eventRepo.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Get Event dengan ID " + eventId + " tidak dapat ditemukan"));

        log.info("End get event by id {}", eventId);
        return mapEventResponse(e);
    }

    @Transactional(readOnly = true)
    public PageEnvelope<EventResponse> listEvents(BasePaging paging, LocalDateTime from, LocalDateTime to) {
        log.info("Start list events");

        // Normalize sort and direction
        String sortField = normalizeSortField(paging.sortField());
        Sort.Direction sortDir = normalizeSortDir(paging.sortDirection());

        Pageable pageable = PageRequest.of(paging.pageIndex(), paging.perpage(), Sort.by(sortDir, sortField));
        log.info("[paging] pageIndex={} perpage={} sortField={} dir={}", paging.pageIndex(), paging.perpage(), sortField, sortDir);

        // Build specification
        Specification<Event> spec = ((root, query, criteriaBuilder) -> {
            List<Predicate> filters = new ArrayList<>();
            if (paging.q() != null && !paging.q().isBlank()) {
                String likePattern = "%" + paging.q().trim().toLowerCase() + "%";
                filters.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(TITLE)), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(LOCATION)), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
                ));
                log.info("[filter] q='{}' on [title,location,description] (LOWER LIKE)", paging.q().trim());
            }

            // Filter by startTime range
            if (from != null) {
                filters.add(criteriaBuilder.greaterThanOrEqualTo(root.get(START_TIME), from));
                log.info("[filter] from={}", from);
            }
            if (to != null) {
                filters.add(criteriaBuilder.lessThanOrEqualTo(root.get(START_TIME), to));
                log.info("[filter] to={}", to);
            }
            if (filters.isEmpty()) return criteriaBuilder.conjunction();
            return criteriaBuilder.and(filters.toArray(new Predicate[0]));
        });

        // Execute query
        Page<Event> page = eventRepo.findAll(spec, pageable);
        List<EventResponse> content = page.getContent().stream()
                .map(EventService::mapEventResponse)
                .toList();

        SortMeta meta = new SortMeta(sortField, sortDir.name().toLowerCase());
        return PageEnvelope.of(paging, page.getTotalElements(), content, meta);
    }

    @Transactional
    public EventResponse updateEvent(UUID eventId, EventUpdateRequest req) {
        log.info("Start update event {}", eventId);

        Event e = eventRepo.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Update Event dengan ID " + eventId + " update tidak ditemukan"));

        if (req.endTime().isBefore(req.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        e.setTitle(req.title());
        e.setStartTime(req.startTime());
        e.setEndTime(req.endTime());
        e.setLocation(req.location());
        e.setQuota(req.quota());
        e.setDescription(req.description());

        Event updatedEvent = eventRepo.save(e);

        log.info("End update event {}", eventId);
        return mapEventResponse(updatedEvent);
    }

    // ----------------- Registration Participant ----------------
    @Transactional
    public ParticipantResponse registerParticipant(UUID eventId, RegisterParticipantRequest req) {
        log.info("Start register participant for event {}", eventId);

        Event e = eventRepo.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event dengan ID " + eventId + " tidak ditemukan"));

        // Validate wargaNik exists in identity service
        if (!identityClientFacade.existsWargaByNik(req.wargaNik())) {
            throw new DataNotFoundException("Warga dengan NIK " + req.wargaNik() + " tidak ditemukan di identity service");
        }

        if (participantRepo.existsByEventIdAndWargaNik(eventId, req.wargaNik())) {
            throw new ConflictException("Warga dengan NIK " + req.wargaNik() + " sudah terdaftar pada event ini");
        }

        long currentParticipants = participantRepo.findByEvent(e).size();
        if (currentParticipants >= e.getQuota()) {
            throw new IllegalStateException("Kuota event sudah penuh");
        }

        EventParticipant p = EventParticipant.builder()
                .event(e)
                .wargaNik(req.wargaNik())
                .build();
        EventParticipant savedParticipant = participantRepo.save(p);

        log.info("End register participant for event {}", eventId);
        return mapParticipantResponse(savedParticipant);
    }

    @Transactional(readOnly = true)
    public List<ParticipantResponse> listParticipants(UUID eventId) {
        log.info("Start list participants for event {}", eventId);

        Event e = eventRepo.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event dengan ID " + eventId + " tidak ditemukan"));

        List<EventParticipant> participants = participantRepo.findByEvent(e);
        List<ParticipantResponse> response = participants.stream()
                .map(EventService::mapParticipantResponse)
                .toList();

        log.info("End list participants for event {}", eventId);
        return response;
    }

    private static EventResponse mapEventResponse(Event e){
        return new EventResponse(
                e.getId().toString(), e.getTitle(), e.getStartTime(), e.getEndTime(),
                e.getLocation(), e.getQuota(), e.getDescription()
        );
    }
    private static ParticipantResponse mapParticipantResponse(EventParticipant p){
        return new ParticipantResponse(
                p.getId().toString(),
                p.getEvent().getId().toString(),
                p.getWargaNik()
        );
    }

    private String normalizeSortField(String f){
        if (f==null || f.isBlank()) return START_TIME;
        return switch (f){
            case TITLE -> TITLE;
            case "endTime" -> "endTime";
            case LOCATION -> LOCATION;
            case "quota" -> "quota";
            default -> START_TIME;
        };
    }
    private Sort.Direction normalizeSortDir(String d){
        return "asc".equalsIgnoreCase(d) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}
