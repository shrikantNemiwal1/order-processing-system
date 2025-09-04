package org.example.domain;

import org.example.events.Event;
import org.example.events.OrderCreatedEvent;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DomainModelTest {

    @Test
    public void testOrderCreation() {
        List<OrderItem> items = Arrays.asList(
                new OrderItem("P001", 2),
                new OrderItem("P002", 1)
        );

        Order order = new Order("ORD001", "CUST001", items, 150.0);

        assertEquals("ORD001", order.getOrderId());
        assertEquals("CUST001", order.getCustomerId());
        assertEquals(150.0, order.getTotalAmount(), 0.01);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(2, order.getItems().size());
        assertTrue(order.getEventHistory().isEmpty());
    }

    @Test
    public void testOrderItemCreation() {
        OrderItem item = new OrderItem("P001", 5);

        assertEquals("P001", item.getItemId());
        assertEquals(5, item.getQuantity());
    }

    @Test
    public void testOrderStatusValues() {
        // Test that all expected status values exist
        assertEquals("PENDING", OrderStatus.PENDING.name());
        assertEquals("PAID", OrderStatus.PAID.name());
        assertEquals("PARTIALLY_PAID", OrderStatus.PARTIALLY_PAID.name());
        assertEquals("SHIPPED", OrderStatus.SHIPPED.name());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
    }

    @Test
    public void testOrderEventHistory() {
        Order order = new Order();
        Event event = new OrderCreatedEvent("e1", LocalDateTime.now(), "ORD001", "CUST001",
                Arrays.asList(new OrderItem("P001", 1)), 50.0);

        order.addEventToHistory(event);

        assertEquals(1, order.getEventHistory().size());
        assertEquals("e1", order.getEventHistory().get(0).getEventId());
    }

    @Test
    public void testOrderImmutability() {
        List<OrderItem> originalItems = Arrays.asList(new OrderItem("P001", 2));
        Order order = new Order("ORD001", "CUST001", originalItems, 100.0);

        // Get items and try to modify
        List<OrderItem> retrievedItems = order.getItems();
        retrievedItems.add(new OrderItem("P002", 1)); // This should not affect original

        // Original order should still have 1 item
        assertEquals(1, order.getItems().size());
    }
}
