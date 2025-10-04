package com.yolifay.eventservice.controller;

import com.yolifay.eventservice.common.Constants;
import com.yolifay.eventservice.common.ConstantsProperties;
import com.yolifay.eventservice.common.ResponseApiService;
import com.yolifay.eventservice.common.ResponseApiUtil;
import com.yolifay.eventservice.dto.EventCreateRequest;
import com.yolifay.eventservice.dto.EventResponse;
import com.yolifay.eventservice.dto.ParticipantResponse;
import com.yolifay.eventservice.dto.RegisterParticipantRequest;
import com.yolifay.eventservice.dto.pagination.BasePaging;
import com.yolifay.eventservice.dto.pagination.PageEnvelope;
import com.yolifay.eventservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;
    private final ConstantsProperties constantsProperties;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApiService<EventResponse>> createEvent(@RequestBody @Valid EventCreateRequest req) {
        log.info("Incoming create event");

        EventResponse response = eventService.createEvent(req);

        log.info("Outgoing create event");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.OK.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.CREATED,
                        response
                )
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApiService<PageEnvelope<EventResponse>>> listEvents(
            @RequestParam(required=false) Integer page,
            @RequestParam(required=false, name="perpage") Integer perPage,
            @RequestParam(required=false, name="sortField") String sortField,
            @RequestParam(required=false, name="sortDirection") String sortDirection,
            @RequestParam(required=false, name="q") String q,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ){
        log.info("Incoming search event");

        var paging = new BasePaging(page, perPage, sortField, sortDirection, q);
        var response = eventService.listEvents(paging, from, to);

        log.info("Outgoing search event");
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.OK.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.APPROVED,
                        response
                )
        );
    }

    @GetMapping(value = "/by-id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApiService<EventResponse>> getEventById(@PathVariable UUID id) {
        log.info("Incoming get event by id: {}", id);

        EventResponse response = eventService.getEventById(id);

        log.info("Outgoing get event by id: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.OK.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.APPROVED,
                        response
                )
        );
    }

    @PostMapping(value="/{id}/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApiService<ParticipantResponse>> register(@PathVariable UUID id, @RequestBody @Valid RegisterParticipantRequest req){
        log.info("Incoming participant register");

        var response = eventService.registerParticipant(id, req);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.CREATED.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.CREATED,
                        response
                )
        );
    }

    @GetMapping(value="/{id}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApiService<List<ParticipantResponse>>> listParticipants(@PathVariable UUID id){
        log.info("Incoming list participants");

        var response = eventService.listParticipants(id);

        log.info("Outgoing list participants");
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.OK.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.APPROVED,
                        response
                )
        );
    }
}
