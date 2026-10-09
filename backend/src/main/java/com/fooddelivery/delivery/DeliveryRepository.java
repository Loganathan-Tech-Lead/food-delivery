package com.fooddelivery.delivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Optional<Delivery> findByOrderId(UUID orderId);
    List<Delivery> findByStatusNotIn(List<String> statuses);
}