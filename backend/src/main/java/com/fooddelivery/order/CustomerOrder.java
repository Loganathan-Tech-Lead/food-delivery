package com.fooddelivery.order;

import com.fooddelivery.restaurant.Restaurant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class CustomerOrder {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected CustomerOrder() {}

    public CustomerOrder(Restaurant restaurant, String customerId, BigDecimal total) {
        this.id = UUID.randomUUID();
        this.restaurant = restaurant;
        this.customerId = customerId;
        this.total = total;
        this.status = OrderStatus.CONFIRMED;
        this.paymentStatus = "SIMULATED_CONFIRMED";
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public Restaurant getRestaurant() { return restaurant; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setStatus(OrderStatus status) { this.status = status; }
}