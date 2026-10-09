package com.fooddelivery.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public final class OrderRequests {
    private OrderRequests() {}

    public record CreateOrderRequest(@NotNull UUID restaurantId, @Size(max = 120) String customerId,
                                     @NotEmpty @Valid List<OrderLineRequest> items) {}

    public record OrderLineRequest(@NotNull UUID menuItemId, @Min(1) int quantity) {}
}