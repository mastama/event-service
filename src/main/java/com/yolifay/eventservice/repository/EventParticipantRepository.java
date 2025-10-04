package com.yolifay.eventservice.repository;

import com.yolifay.eventservice.entity.Event;
import com.yolifay.eventservice.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    List<EventParticipant> findByEvent(Event event);
    boolean existsByEventIdAndWargaNik(UUID eventId, String wargaNik);
}
