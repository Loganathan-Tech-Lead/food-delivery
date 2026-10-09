package com.fooddelivery.delivery;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "delivery_locations")
public class DeliveryLocation {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;
    @Column(nullable = false, precision = 9, scale = 6) private BigDecimal latitude;
    @Column(nullable = false, precision = 9, scale = 6) private BigDecimal longitude;
    @Column(name = "recorded_at", nullable = false) private Instant recordedAt;
    protected DeliveryLocation() {}
    public DeliveryLocation(Delivery delivery, BigDecimal latitude, BigDecimal longitude) {
        this.id = UUID.randomUUID();
        this.delivery = delivery;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = Instant.now();
    }
}