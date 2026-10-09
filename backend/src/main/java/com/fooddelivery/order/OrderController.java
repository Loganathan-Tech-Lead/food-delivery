package com.fooddelivery.order;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<OrderResponses.OrderResponse> create(@Valid @RequestBody OrderRequests.CreateOrderRequest request) {
        OrderResponses.OrderResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    @GetMapping("/{orderId}")
    public OrderResponses.OrderResponse get(@PathVariable UUID orderId) { return service.get(orderId); }

    @GetMapping
    public List<OrderResponses.OrderResponse> list(@RequestParam(required = false) String customerId) {
        return service.list(customerId);
    }
}