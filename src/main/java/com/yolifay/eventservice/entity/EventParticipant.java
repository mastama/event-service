package com.yolifay.eventservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @Table(
        name = "event_participant",
        uniqueConstraints = @UniqueConstraint(name = "uq_event_warga", columnNames = {"event_id","warga_nik"})
)
public class EventParticipant {
    @Id
    private UUID id;

    @PrePersist void pre(){ if (id==null) id = UUID.randomUUID(); }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "warga_nik", length = 16, nullable = false)
    private String wargaNik; // decoupled dari identity-service, cukup simpan NIK
}
