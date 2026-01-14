package com.livingcostcheck.home_repair.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_log")
@Data
@NoArgsConstructor
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verdict_id", nullable = false)
    private UUID verdictId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private String target;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum EventType {
        CLICK_AD,
        CLICK_AFFILIATE,
        SUBMIT_EMAIL
    }

    public EventLog(UUID verdictId, EventType eventType, String target) {
        this.verdictId = verdictId;
        this.eventType = eventType;
        this.target = target;
    }
}
