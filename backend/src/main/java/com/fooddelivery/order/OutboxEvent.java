package com.fooddelivery.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    @Column(name = "event_id")
    private UUID eventId;
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(nullable = false)
    private String topic;
    @Column(nullable = false, columnDefinition = "text")
    private String payload;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "published_at")
    private Instant publishedAt;
    private int attempts;

    protected OutboxEvent() {}

    public OutboxEvent(UUID eventId, UUID aggregateId, String eventType, String topic, String payload) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public String getTopic() { return topic; }
    public String getPayload() { return payload; }
    public Instant getPublishedAt() { return publishedAt; }
    public void markPublished() { publishedAt = Instant.now(); }
    public void recordAttempt() { attempts++; }
}