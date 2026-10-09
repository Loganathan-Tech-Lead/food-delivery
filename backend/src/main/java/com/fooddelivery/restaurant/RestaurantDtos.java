package com.fooddelivery.restaurant;

import java.math.BigDecimal;
import java.util.UUID;

public final class RestaurantDtos {
    private RestaurantDtos() {}

    public record RestaurantResponse(UUID id, String name, String description, String cuisine,
                                     BigDecimal rating, int deliveryMinutes, String imageUrl) {
        static RestaurantResponse from(Restaurant restaurant) {
            return new RestaurantResponse(restaurant.getId(), restaurant.getName(), restaurant.getDescription(),
                    restaurant.getCuisine(), restaurant.getRating(), restaurant.getDeliveryMinutes(), restaurant.getImageUrl());
        }
    }

    public record MenuItemResponse(UUID id, UUID restaurantId, String name, String description,
                                   BigDecimal price, boolean available, String imageUrl) {
        static MenuItemResponse from(MenuItem item) {
            return new MenuItemResponse(item.getId(), item.getRestaurant().getId(), item.getName(), item.getDescription(),
                    item.getPrice(), item.isAvailable(), item.getImageUrl());
        }
    }
}