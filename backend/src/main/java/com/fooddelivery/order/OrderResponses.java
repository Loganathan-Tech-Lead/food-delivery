package com.fooddelivery.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class OrderResponses {
    private OrderResponses() {}

    public record OrderItemResponse(UUID menuItemId, String name, int quantity, BigDecimal unitPrice,
                                    BigDecimal lineTotal) {}
    public record OrderResponse(UUID id, UUID restaurantId, String restaurantName, String customerId,
                                BigDecimal total, OrderStatus status, String paymentStatus,
                                Instant createdAt, List<OrderItemResponse> items) {}
    public record OrderCreatedEvent(UUID eventId, UUID orderId, UUID restaurantId, String customerId,
                                    BigDecimal orderTotal, Instant timestamp) {}
}