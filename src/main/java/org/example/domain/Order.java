package org.example.domain;

import org.example.events.Event;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order, including items, status, and event history.
 */
public class Order {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private double totalAmount;
    private OrderStatus status;
    private final List<Event> eventHistory;

    public Order() {
        this.items = new ArrayList<>();
        this.eventHistory = new ArrayList<>();
        this.status = OrderStatus.PENDING;
    }

    public Order(String orderId, String customerId, List<OrderItem> items, double totalAmount) {
        this();
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
    }

    public void addEventToHistory(Event event) {
        this.eventHistory.add(event);
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
        return new ArrayList<>(items);
    }

    public void setItems(List<OrderItem> items) {
        this.items = new ArrayList<>(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<Event> getEventHistory() {
        return new ArrayList<>(eventHistory);
    }

    @Override
    public String toString() {
        return String.format("Order{orderId='%s', customerId='%s', status=%s, totalAmount=%.2f, items=%s}",
                orderId, customerId, status, totalAmount, items);
    }
}
