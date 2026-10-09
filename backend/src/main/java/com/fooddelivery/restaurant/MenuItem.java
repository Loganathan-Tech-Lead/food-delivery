package com.fooddelivery.restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;
    @Column(name = "image_url")
    private String imageUrl;

    protected MenuItem() {}

    public UUID getId() { return id; }
    public Restaurant getRestaurant() { return restaurant; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public String getImageUrl() { return imageUrl; }
}