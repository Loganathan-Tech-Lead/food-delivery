package com.fooddelivery.delivery;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    @Column(name = "event_id")
    private UUID eventId;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected ProcessedEvent() {}

    public ProcessedEvent(UUID eventId, String eventType, UUID orderId) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.processedAt = Instant.now();
    }
}