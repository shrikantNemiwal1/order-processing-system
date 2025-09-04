package org.example.events;

import org.example.domain.OrderItem;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EventsTest {

    @Test
    public void testOrderCreatedEvent() {
        LocalDateTime timestamp = LocalDateTime.now();
        List<OrderItem> items = Arrays.asList(new OrderItem("P001", 2));

        OrderCreatedEvent event = new OrderCreatedEvent(
                "e1", timestamp, "ORD001", "CUST001", items, 100.0
        );

        assertEquals("e1", event.getEventId());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("OrderCreated", event.getEventType());
        assertEquals("ORD001", event.getOrderId());
        assertEquals("CUST001", event.getCustomerId());
        assertEquals(100.0, event.getTotalAmount(), 0.01);
        assertEquals(1, event.getItems().size());
    }

    @Test
    public void testPaymentReceivedEvent() {
        LocalDateTime timestamp = LocalDateTime.now();

        PaymentReceivedEvent event = new PaymentReceivedEvent(
                "e2", timestamp, "ORD001", 150.0
        );

        assertEquals("e2", event.getEventId());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("PaymentReceived", event.getEventType());
        assertEquals("ORD001", event.getOrderId());
        assertEquals(150.0, event.getAmountPaid(), 0.01);
    }

    @Test
    public void testShippingScheduledEvent() {
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime shippingDate = LocalDateTime.now().plusDays(3);

        ShippingScheduledEvent event = new ShippingScheduledEvent(
                "e3", timestamp, "ORD001", shippingDate
        );

        assertEquals("e3", event.getEventId());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("ShippingScheduled", event.getEventType());
        assertEquals("ORD001", event.getOrderId());
        assertEquals(shippingDate, event.getShippingDate());
    }

    @Test
    public void testOrderCancelledEvent() {
        LocalDateTime timestamp = LocalDateTime.now();
        String reason = "Customer requested cancellation";

        OrderCancelledEvent event = new OrderCancelledEvent(
                "e4", timestamp, "ORD001", reason
        );

        assertEquals("e4", event.getEventId());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("OrderCancelled", event.getEventType());
        assertEquals("ORD001", event.getOrderId());
        assertEquals(reason, event.getReason());
    }

    @Test
    public void testBaseEventProperties() {
        LocalDateTime timestamp = LocalDateTime.now();

        Event event = new Event("e1", timestamp, "TestEvent") {};

        assertEquals("e1", event.getEventId());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("TestEvent", event.getEventType());
    }
}
