package com.fooddelivery.delivery;
import com.fooddelivery.order.CustomerOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private CustomerOrder order;
    @Column(name = "partner_name", nullable = false) private String partnerName;
    @Column(name = "partner_phone", nullable = false) private String partnerPhone;
    @Column(nullable = false) private String status;
    @Column(nullable = false, precision = 9, scale = 6) private BigDecimal latitude;
    @Column(nullable = false, precision = 9, scale = 6) private BigDecimal longitude;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Delivery() {}
    public Delivery(CustomerOrder order, String partnerName, String partnerPhone, BigDecimal latitude, BigDecimal longitude) {
        this.id = UUID.randomUUID();
        this.order = order;
        this.partnerName = partnerName;
        this.partnerPhone = partnerPhone;
        this.status = "ASSIGNED";
        this.latitude = latitude;
        this.longitude = longitude;
    }
    @PrePersist void onCreate() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public CustomerOrder getOrder() { return order; }
    public String getPartnerName() { return partnerName; }
    public String getPartnerPhone() { return partnerPhone; }
    public String getStatus() { return status; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void update(String status, BigDecimal latitude, BigDecimal longitude) {
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}