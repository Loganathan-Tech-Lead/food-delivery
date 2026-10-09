package com.fooddelivery.order;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    PICKED_UP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}