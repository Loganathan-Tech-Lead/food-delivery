package com.fooddelivery.delivery;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class DeliverySimulation {
    private static final Logger log = LoggerFactory.getLogger(DeliverySimulation.class);
    private final DeliveryRepository deliveries;
    private final DeliveryService deliveryService;
    public DeliverySimulation(DeliveryRepository deliveries, DeliveryService deliveryService) {
        this.deliveries = deliveries;
        this.deliveryService = deliveryService;
    }
    @Scheduled(fixedDelayString = "${app.delivery.simulation-interval-ms:15000}")
    @Transactional
    public void moveDeliveries() {
        List<Delivery> active = deliveries.findByStatusNotIn(List.of("DELIVERED", "CANCELLED"));
        for (Delivery delivery : active) {
            try {
                deliveryService.advance(delivery);
            } catch (RuntimeException exception) {
                log.error("Could not advance delivery orderId={}", delivery.getOrder().getId(), exception);
            }
        }
    }
}