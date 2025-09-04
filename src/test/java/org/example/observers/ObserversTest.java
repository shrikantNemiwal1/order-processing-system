package org.example.observers;

import org.example.domain.Order;
import org.example.domain.OrderItem;
import org.example.events.Event;
import org.example.events.OrderCreatedEvent;
import org.example.events.OrderCancelledEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ObserversTest {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private LoggerObserver loggerObserver;
    private AlertObserver alertObserver;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
        loggerObserver = new LoggerObserver();
        alertObserver = new AlertObserver();
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testLoggerObserverStatusChange() {
        Order order = new Order("ORD001", "CUST001",
                Arrays.asList(new OrderItem("P001", 1)), 50.0);

        loggerObserver.onOrderStatusChanged(order, "PENDING", "PAID");

        String output = outputStream.toString();
        assertTrue("Should log status change",
                output.contains("[LOGGER] Order ORD001 status changed from PENDING to PAID"));
    }

    @Test
    public void testLoggerObserverEventProcessed() {
        Order order = new Order("ORD001", "CUST001",
                Arrays.asList(new OrderItem("P001", 1)), 50.0);
        Event event = new OrderCreatedEvent("e1", LocalDateTime.now(),
                "ORD001", "CUST001",
                Arrays.asList(new OrderItem("P001", 1)), 50.0);

        loggerObserver.onEventProcessed(event, order);

        String output = outputStream.toString();
        assertTrue("Should log event processing",
                output.contains("[LOGGER] Event processed - Type: OrderCreated"));
        assertTrue("Should include event ID", output.contains("EventId: e1"));
        assertTrue("Should include order ID", output.contains("OrderId: ORD001"));
    }

    @Test
    public void testAlertObserverCriticalStatusChange() {
        Order order = new Order("ORD001", "CUST001",
                Arrays.asList(new OrderItem("P001", 1)), 50.0);

        // Test SHIPPED status (critical)
        alertObserver.onOrderStatusChanged(order, "PAID", "SHIPPED");

        String output = outputStream.toString();
        assertTrue("Should send alert for SHIPPED status",
                output.contains("[ALERT] Sending alert for Order ORD001: Status changed to SHIPPED"));
    }

    @Test
    public void testAlertObserverNonCriticalStatusChange() {
        Order order = new Order("ORD001", "CUST001",
                Arrays.asList(new OrderItem("P001", 1)), 50.0);

        // Test PAID status (non-critical)
        alertObserver.onOrderStatusChanged(order, "PENDING", "PAID");

        String output = outputStream.toString();
        assertFalse("Should not send alert for non-critical status",
                output.contains("[ALERT]"));
    }

    @Test
    public void testAlertObserverCancellationEvent() {
        Order order = new Order("ORD001", "CUST001",
                Arrays.asList(new OrderItem("P001", 1)), 50.0);
        Event cancelEvent = new OrderCancelledEvent("e1", LocalDateTime.now(),
                "ORD001", "Customer request");

        alertObserver.onEventProcessed(cancelEvent, order);

        String output = outputStream.toString();
        assertTrue("Should alert on cancellation event",
                output.contains("[ALERT] Critical event: Order ORD001 has been cancelled"));
    }
}
