package com.fooddelivery.order;

import com.fooddelivery.restaurant.MenuItem;
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
@Table(name = "order_items")
public class OrderItem {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
    @Column(name = "dish_name", nullable = false)
    private String dishName;
    private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    protected OrderItem() {}

    public OrderItem(MenuItem menuItem, int quantity) {
        this.id = UUID.randomUUID();
        this.menuItem = menuItem;
        this.dishName = menuItem.getName();
        this.quantity = quantity;
        this.unitPrice = menuItem.getPrice();
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    void setOrder(CustomerOrder order) { this.order = order; }
    public MenuItem getMenuItem() { return menuItem; }
    public String getDishName() { return dishName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
}