package com.fooddelivery.restaurant;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {}