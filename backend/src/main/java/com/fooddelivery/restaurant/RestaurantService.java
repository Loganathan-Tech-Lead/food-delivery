package com.fooddelivery.restaurant;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;

    public RestaurantService(RestaurantRepository restaurants, MenuItemRepository menuItems) {
        this.restaurants = restaurants;
        this.menuItems = menuItems;
    }

    public List<RestaurantDtos.RestaurantResponse> list() {
        return restaurants.findAll().stream().map(RestaurantDtos.RestaurantResponse::from).toList();
    }

    public RestaurantDtos.RestaurantResponse get(UUID id) {
        return restaurants.findById(id).map(RestaurantDtos.RestaurantResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
    }

    public List<RestaurantDtos.MenuItemResponse> menu(UUID restaurantId) {
        if (!restaurants.existsById(restaurantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
        }
        return menuItems.findByRestaurantIdOrderByName(restaurantId).stream()
                .map(RestaurantDtos.MenuItemResponse::from).toList();
    }
}