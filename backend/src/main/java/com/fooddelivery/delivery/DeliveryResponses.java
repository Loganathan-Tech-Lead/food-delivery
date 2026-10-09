package com.fooddelivery.delivery;
import com.fooddelivery.order.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public final class DeliveryResponses {
    private DeliveryResponses() {}
    public record LocationResponse(BigDecimal latitude, BigDecimal longitude, Instant updatedAt, boolean simulated) {}
    public record DeliveryResponse(UUID orderId, OrderStatus orderStatus, String deliveryStatus,
            String partnerName, String partnerPhone, LocationResponse location, List<OrderStatus> timeline) {}
    public record DeliveryAssignedEvent(UUID eventId, UUID orderId, String partnerName, String partnerPhone,
            String status, Instant timestamp) {}
    public record DeliveryStatusEvent(UUID eventId, UUID orderId, OrderStatus orderStatus,
            String deliveryStatus, Instant timestamp) {}
    public record DeliveryLocationEvent(UUID eventId, UUID orderId, BigDecimal latitude,
            BigDecimal longitude, Instant timestamp, boolean simulated) {}
}