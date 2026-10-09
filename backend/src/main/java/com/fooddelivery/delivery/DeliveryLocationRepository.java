package com.fooddelivery.delivery;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DeliveryLocationRepository extends JpaRepository<DeliveryLocation, UUID> {
    Optional<DeliveryLocation> findFirstByDeliveryIdOrderByRecordedAtDesc(UUID deliveryId);
}