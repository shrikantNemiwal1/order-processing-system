package org.example.processing;

import org.example.domain.Order;
import org.example.domain.OrderItem;
import org.example.domain.OrderStatus;
import org.example.events.*;
import org.example.observers.OrderObserver;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class EventProcessorTest {
    private EventProcessor processor;
    private TestObserver testObserver;

    @Before
    public void setUp() {
        processor = new EventProcessor();
        testObserver = new TestObserver();
        processor.addObserver(testObserver);
    }

    @Test
    public void testOrderCreation() {
        List<OrderItem> items = Arrays.asList(new OrderItem("P001", 2));
        OrderCreatedEvent event = new OrderCreatedEvent(
                "e1", LocalDateTime.now(), "ORD001", "CUST001", items, 100.0
        );

        processor.processEvent(event);
        Order order = processor.getOrders().get("ORD001");

        assertNotNull("Order should be created", order);
        assertEquals("ORD001", order.getOrderId());
        assertEquals("CUST001", order.getCustomerId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(100.0, order.getTotalAmount(), 0.01);
        assertEquals(1, order.getEventHistory().size());
        assertEquals(1, testObserver.eventProcessedCount.get());
    }

    @Test
    public void testFullPaymentReceived() {
        // First create order
        List<OrderItem> items = Arrays.asList(new OrderItem("P001", 2));
        OrderCreatedEvent createEvent = new OrderCreatedEvent(
                "e1", LocalDateTime.now(), "ORD002", "CUST002", items, 100.0
        );
        processor.processEvent(createEvent);

        // Then process payment
        PaymentReceivedEvent paymentEvent = new PaymentReceivedEvent(
                "e2", LocalDateTime.now(), "ORD002", 100.0
        );
        processor.processEvent(paymentEvent);

        Order order = processor.getOrders().get("ORD002");
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(2, order.getEventHistory().size());
        assertEquals(1, testObserver.statusChangeCount.get());
    }

    @Test
    public void testPartialPaymentReceived() {
        // Create order
        List<OrderItem> items = Arrays.asList(new OrderItem("P001", 3));
        OrderCreatedEvent createEvent = new OrderCreatedEvent(
                "e1", LocalDateTime.now(), "ORD003", "CUST003", items, 200.0
        );
        processor.processEvent(createEvent);

        // Process partial payment
        PaymentReceivedEvent partialPayment = new PaymentReceivedEvent(
                "e2", LocalDateTime.now(), "ORD003", 150.0
        );
        processor.processEvent(partialPayment);

        Order order = processor.getOrders().get("ORD003");
        assertEquals(OrderStatus.PARTIALLY_PAID, order.getStatus());
        assertTrue(testObserver.statusChanged.get());
    }

    @Test
    public void testShippingScheduled() {
        // Create and pay for order
        List<OrderItem> items = Arrays.asList(new OrderItem("P001", 1));
        OrderCreatedEvent createEvent = new OrderCreatedEvent(
                "e1", LocalDateTime.now(), "ORD004", "CUST004", items, 50.0
        );
        processor.processEvent(createEvent);

        PaymentReceivedEvent paymentEvent = new PaymentReceivedEvent(
                "e2", LocalDateTime.now(), "ORD004", 50.0
        );
        processor.processEvent(paymentEvent);

        // Schedule shipping
        ShippingScheduledEvent shippingEvent = new ShippingScheduledEvent(
                "e3", LocalDateTime.now(), "ORD004", LocalDateTime.now().plusDays(2)
        );
        processor.processEvent(shippingEvent);

        Order order = processor.getOrders().get("ORD004");
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(3, order.getEventHistory().size());
    }

    @Test
    public void testOrderCancellation() {
        // Create order
        List<OrderItem> items = Arrays.asList(new OrderItem("P001", 1));
        OrderCreatedEvent createEvent = new OrderCreatedEvent(
                "e1", LocalDateTime.now(), "ORD005", "CUST005", items, 75.0
        );
        processor.processEvent(createEvent);

        // Cancel order
        OrderCancelledEvent cancelEvent = new OrderCancelledEvent(
                "e2", LocalDateTime.now(), "ORD005", "Customer requested cancellation"
        );
        processor.processEvent(cancelEvent);

        Order order = processor.getOrders().get("ORD005");
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(2, order.getEventHistory().size());
    }

    @Test
    public void testPaymentForNonExistentOrder() {
        PaymentReceivedEvent paymentEvent = new PaymentReceivedEvent(
                "e1", LocalDateTime.now(), "NONEXISTENT", 100.0
        );

        // Should not throw exception
        processor.processEvent(paymentEvent);

        // Order should not be created
        assertNull(processor.getOrders().get("NONEXISTENT"));
    }

    @Test
    public void testUnknownEventType() {
        Event unknownEvent = new Event("e1", LocalDateTime.now(), "UnknownType") {};

        // Should handle gracefully without throwing exception
        processor.processEvent(unknownEvent);

        // No orders should be created
        assertTrue(processor.getOrders().isEmpty());
    }

    @Test
    public void testObserverNotification() {
        List<OrderItem> items = Arrays.asList(new OrderItem("P001", 1));
        OrderCreatedEvent event = new OrderCreatedEvent(
                "e1", LocalDateTime.now(), "ORD006", "CUST006", items, 100.0
        );

        processor.processEvent(event);

        assertTrue("Observer should be notified of event processing",
                testObserver.eventProcessedCount.get() > 0);
    }

    // Test helper class
    private static class TestObserver implements OrderObserver {
        AtomicInteger statusChangeCount = new AtomicInteger(0);
        AtomicInteger eventProcessedCount = new AtomicInteger(0);
        AtomicBoolean statusChanged = new AtomicBoolean(false);

        @Override
        public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
            statusChangeCount.incrementAndGet();
            statusChanged.set(true);
        }

        @Override
        public void onEventProcessed(Event event, Order order) {
            eventProcessedCount.incrementAndGet();
        }
    }
}
