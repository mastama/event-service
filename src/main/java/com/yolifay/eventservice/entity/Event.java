package com.yolifay.eventservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "event")
public class Event {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @PrePersist void pre(){ if (id==null) id = UUID.randomUUID(); }

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private String location;

    @Column(nullable = false)
    private Integer quota;

    @Column(columnDefinition = "text")
    private String description;

    @CreationTimestamp @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
