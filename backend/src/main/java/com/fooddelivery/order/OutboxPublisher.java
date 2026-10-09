package com.fooddelivery.order;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private final OutboxEventRepository outbox;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxEventRepository outbox, KafkaTemplate<String, String> kafkaTemplate) {
        this.outbox = outbox;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1_000L)
    @Transactional
    public void publishPending() {
        for (OutboxEvent event : outbox.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getAggregateId().toString(), event.getPayload())
                        .get(10, TimeUnit.SECONDS);
                event.markPublished();
                log.info("Published eventId={} orderId={} topic={}", event.getEventId(), event.getAggregateId(), event.getTopic());
            } catch (Exception exception) {
                event.recordAttempt();
                log.error("Failed publishing eventId={} orderId={} topic={}", event.getEventId(), event.getAggregateId(), event.getTopic(), exception);
            }
        }
    }
}