package com.fooddelivery.restaurant;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByRestaurantIdOrderByName(UUID restaurantId);
}