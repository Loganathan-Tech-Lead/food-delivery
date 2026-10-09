package com.fooddelivery.delivery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.order.CustomerOrder;
import com.fooddelivery.order.OutboxEvent;
import com.fooddelivery.order.OutboxEventRepository;
import com.fooddelivery.order.OrderRepository;
import com.fooddelivery.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@Service
public class DeliveryService {
    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private static final List<OrderStatus> TIMELINE = List.of(OrderStatus.CREATED, OrderStatus.CONFIRMED,
            OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP, OrderStatus.PICKED_UP,
            OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED);
    private final ProcessedEventRepository processedEvents;
    private final DeliveryRepository deliveries;
    private final DeliveryLocationRepository locations;
    private final OrderRepository orders;
    private final OutboxEventRepository outbox;
    private final ObjectMapper objectMapper;
    private final DeliveryUpdateStream stream;
    public DeliveryService(ProcessedEventRepository processedEvents, DeliveryRepository deliveries,
            DeliveryLocationRepository locations, OrderRepository orders, OutboxEventRepository outbox,
            ObjectMapper objectMapper, DeliveryUpdateStream stream) {
        this.processedEvents = processedEvents;
        this.deliveries = deliveries;
        this.locations = locations;
        this.orders = orders;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
        this.stream = stream;
    }
    @KafkaListener(topics = "food.order.created")
    @Transactional
    public void onOrderCreated(String payload) throws JsonProcessingException {
        JsonNode event = objectMapper.readTree(payload);
        UUID eventId = UUID.fromString(event.required("eventId").asText());
        UUID orderId = UUID.fromString(event.required("orderId").asText());
        log.info("Processing OrderCreated eventId={} orderId={}", eventId, orderId);
        if (processedEvents.existsById(eventId)) {
            log.info("Ignoring duplicate eventId={} orderId={}", eventId, orderId);
            return;
        }
        CustomerOrder order = orders.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found for event: " + orderId));
        if (deliveries.findByOrderId(orderId).isEmpty()) {
            int partnerNumber = Math.floorMod(orderId.hashCode(), 8) + 1;
            BigDecimal latitude = new BigDecimal("37.774900");
            BigDecimal longitude = new BigDecimal("-122.419400");
            Delivery delivery = deliveries.save(new Delivery(order, "Rider " + partnerNumber,
                    "+1 (555) 010-" + String.format("%04d", partnerNumber), latitude, longitude));
            locations.save(new DeliveryLocation(delivery, latitude, longitude));
            order.setStatus(OrderStatus.PREPARING);
            emitStatus(order, delivery);
            publishAssigned(order, delivery);
            stream.publish(orderId, "delivery-update", response(order, delivery));
            log.info("Assigned delivery orderId={} deliveryId={} partner={}", orderId, delivery.getId(), delivery.getPartnerName());
        }
        processedEvents.save(new ProcessedEvent(eventId, "OrderCreated", orderId));
    }
    @Transactional(readOnly = true)
    public DeliveryResponses.DeliveryResponse get(UUID orderId) {
        Delivery delivery = deliveries.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not assigned yet"));
        return response(delivery.getOrder(), delivery);
    }
    @Transactional(readOnly = true)
    public DeliveryResponses.LocationResponse location(UUID orderId) {
        Delivery delivery = deliveries.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not assigned yet"));
        return new DeliveryResponses.LocationResponse(delivery.getLatitude(), delivery.getLongitude(), delivery.getUpdatedAt(), true);
    }
    public SseEmitter subscribe(UUID orderId) {
        if (deliveries.findByOrderId(orderId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not assigned yet");
        }
        return stream.subscribe(orderId);
    }
    @Transactional
    public void advance(Delivery delivery) {
        CustomerOrder order = delivery.getOrder();
        OrderStatus next = switch (order.getStatus()) {
            case PREPARING -> OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> OrderStatus.PICKED_UP;
            case PICKED_UP -> OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> OrderStatus.DELIVERED;
            default -> null;
        };
        if (next == null) return;
        BigDecimal latitude = delivery.getLatitude().add(new BigDecimal("0.002500"));
        BigDecimal longitude = delivery.getLongitude().add(new BigDecimal("0.002500"));
        order.setStatus(next);
        delivery.update(next.name(), latitude, longitude);
        locations.save(new DeliveryLocation(delivery, latitude, longitude));
        emitStatus(order, delivery);
        emitLocation(order, delivery);
        stream.publish(order.getId(), "delivery-update", response(order, delivery));
    }
    private DeliveryResponses.DeliveryResponse response(CustomerOrder order, Delivery delivery) {
        return new DeliveryResponses.DeliveryResponse(order.getId(), order.getStatus(), delivery.getStatus(),
                delivery.getPartnerName(), delivery.getPartnerPhone(),
                new DeliveryResponses.LocationResponse(delivery.getLatitude(), delivery.getLongitude(), delivery.getUpdatedAt(), true), TIMELINE);
    }
    private void publishAssigned(CustomerOrder order, Delivery delivery) {
        saveEvent(order.getId(), "DeliveryAssigned", "food.delivery.assigned",
                new DeliveryResponses.DeliveryAssignedEvent(UUID.randomUUID(), order.getId(), delivery.getPartnerName(),
                        delivery.getPartnerPhone(), delivery.getStatus(), Instant.now()));
    }
    private void emitStatus(CustomerOrder order, Delivery delivery) {
        saveEvent(order.getId(), "DeliveryStatusChanged", "food.delivery.status",
                new DeliveryResponses.DeliveryStatusEvent(UUID.randomUUID(), order.getId(), order.getStatus(), delivery.getStatus(), Instant.now()));
    }
    private void emitLocation(CustomerOrder order, Delivery delivery) {
        saveEvent(order.getId(), "DeliveryLocationUpdated", "food.delivery.location",
                new DeliveryResponses.DeliveryLocationEvent(UUID.randomUUID(), order.getId(), delivery.getLatitude(),
                        delivery.getLongitude(), Instant.now(), true));
    }
    private void saveEvent(UUID orderId, String type, String topic, Object payload) {
        try {
            outbox.save(new OutboxEvent(UUID.randomUUID(), orderId, type, topic, objectMapper.writeValueAsString(payload)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize " + type + " event", exception);
        }
    }
}