package org.example.events;

import org.example.domain.OrderItem;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event representing the creation of a new order, including order details.
 */
public class OrderCreatedEvent extends Event {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private double totalAmount;

    public OrderCreatedEvent() {
        super();
    }

    public OrderCreatedEvent(String eventId, LocalDateTime timestamp, String orderId,
            String customerId, List<OrderItem> items, double totalAmount) {
        super(eventId, timestamp, "OrderCreated");
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
