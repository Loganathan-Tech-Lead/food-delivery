package com.fooddelivery.order;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, UUID> {
    List<CustomerOrder> findByCustomerIdOrderByCreatedAtDesc(String customerId);
}