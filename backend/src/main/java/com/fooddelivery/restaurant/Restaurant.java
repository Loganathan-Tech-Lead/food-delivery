package com.fooddelivery.restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    private UUID id;
    private String name;
    private String description;
    private String cuisine;
    private BigDecimal rating;
    @Column(name = "delivery_minutes")
    private int deliveryMinutes;
    @Column(name = "image_url")
    private String imageUrl;

    protected Restaurant() {}

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCuisine() { return cuisine; }
    public BigDecimal getRating() { return rating; }
    public int getDeliveryMinutes() { return deliveryMinutes; }
    public String getImageUrl() { return imageUrl; }
}