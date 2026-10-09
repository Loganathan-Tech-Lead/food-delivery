package com.fooddelivery.restaurant;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<RestaurantDtos.RestaurantResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public RestaurantDtos.RestaurantResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping("/{id}/menu")
    public List<RestaurantDtos.MenuItemResponse> menu(@PathVariable UUID id) {
        return service.menu(id);
    }
}