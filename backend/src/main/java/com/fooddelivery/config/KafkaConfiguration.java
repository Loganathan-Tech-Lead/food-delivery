package com.fooddelivery.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfiguration {
    @Bean
    NewTopic orderCreatedTopic() { return topic("food.order.created"); }

    @Bean
    NewTopic orderCreatedDeadLetterTopic() { return topic("food.order.created.dlq"); }

    @Bean
    NewTopic deliveryAssignedTopic() { return topic("food.delivery.assigned"); }

    @Bean
    NewTopic deliveryLocationTopic() { return topic("food.delivery.location"); }

    @Bean
    NewTopic deliveryStatusTopic() { return topic("food.delivery.status"); }

    @Bean
    DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, exception) -> new TopicPartition(record.topic() + ".dlq", 0));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 3L));
    }

    private NewTopic topic(String name) {
        return new NewTopic(name, 1, (short) 1);
    }
}