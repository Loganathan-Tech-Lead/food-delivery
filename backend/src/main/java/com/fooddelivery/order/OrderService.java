package com.fooddelivery.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.MenuItem;
import com.fooddelivery.restaurant.MenuItemRepository;
import com.fooddelivery.restaurant.Restaurant;
import com.fooddelivery.restaurant.RestaurantRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;
    private final OrderRepository orders;
    private final OutboxEventRepository outbox;
    private final ObjectMapper objectMapper;
    private final String demoCustomerId;

    public OrderService(RestaurantRepository restaurants, MenuItemRepository menuItems, OrderRepository orders,
                        OutboxEventRepository outbox, ObjectMapper objectMapper,
                        @Value("${app.demo-customer-id}") String demoCustomerId) {
        this.restaurants = restaurants;
        this.menuItems = menuItems;
        this.orders = orders;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
        this.demoCustomerId = demoCustomerId;
    }

    @Transactional
    public OrderResponses.OrderResponse create(OrderRequests.CreateOrderRequest request) {
        Restaurant restaurant = restaurants.findById(request.restaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        String customerId = request.customerId() == null || request.customerId().isBlank()
                ? demoCustomerId : request.customerId().trim();
        List<MenuItem> selected = request.items().stream()
                .map(line -> menuItems.findById(line.menuItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item not found: " + line.menuItemId())))
                .toList();
        for (MenuItem item : selected) {
            if (!item.getRestaurant().getId().equals(restaurant.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All items must belong to the selected restaurant");
            }
            if (!item.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, item.getName() + " is currently unavailable");
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        for (int index = 0; index < selected.size(); index++) {
            total = total.add(selected.get(index).getPrice()
                    .multiply(BigDecimal.valueOf(request.items().get(index).quantity())));
        }
        CustomerOrder order = new CustomerOrder(restaurant, customerId, total);
        for (int index = 0; index < selected.size(); index++) {
            order.addItem(new OrderItem(selected.get(index), request.items().get(index).quantity()));
        }
        orders.save(order);

        UUID eventId = UUID.randomUUID();
        OrderResponses.OrderCreatedEvent event = new OrderResponses.OrderCreatedEvent(eventId, order.getId(),
                restaurant.getId(), customerId, total, Instant.now());
        try {
            outbox.save(new OutboxEvent(eventId, order.getId(), "OrderCreated", "food.order.created",
                    objectMapper.writeValueAsString(event)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize OrderCreated event", exception);
        }
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponses.OrderResponse get(UUID id) {
        return orders.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Transactional(readOnly = true)
    public List<OrderResponses.OrderResponse> list(String customerId) {
        String resolvedCustomer = customerId == null || customerId.isBlank() ? demoCustomerId : customerId;
        return orders.findByCustomerIdOrderByCreatedAtDesc(resolvedCustomer).stream().map(this::toResponse).toList();
    }

    private OrderResponses.OrderResponse toResponse(CustomerOrder order) {
        List<OrderResponses.OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderResponses.OrderItemResponse(item.getMenuItem().getId(), item.getDishName(),
                        item.getQuantity(), item.getUnitPrice(), item.getLineTotal())).toList();
        return new OrderResponses.OrderResponse(order.getId(), order.getRestaurant().getId(),
                order.getRestaurant().getName(), order.getCustomerId(), order.getTotal(), order.getStatus(),
                order.getPaymentStatus(), order.getCreatedAt(), items);
    }
}