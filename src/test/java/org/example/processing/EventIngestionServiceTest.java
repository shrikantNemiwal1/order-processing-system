package org.example.processing;

import org.example.events.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class EventIngestionServiceTest {
    private EventIngestionService ingestionService;
    private File testFile;

    @Before
    public void setUp() throws IOException {
        ingestionService = new EventIngestionService();
        testFile = File.createTempFile("test-events", ".json");
    }

    @After
    public void tearDown() {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void testReadValidEvents() throws IOException {
        // Write test data
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("{\"eventId\": \"e1\", \"timestamp\": \"2025-07-29T10:00:00Z\", \"eventType\": \"OrderCreated\", \"orderId\": \"ORD001\", \"customerId\": \"CUST001\", \"items\": [{\"itemId\": \"P001\", \"qty\": 2}], \"totalAmount\": 100.00}\n");
            writer.write("{\"eventId\": \"e2\", \"timestamp\": \"2025-07-29T10:15:00Z\", \"eventType\": \"PaymentReceived\", \"orderId\": \"ORD001\", \"amountPaid\": 100.00}\n");
        }

        List<Event> events = ingestionService.readEventsFromFile(testFile.getAbsolutePath());

        assertEquals(2, events.size());

        // Test first event
        assertTrue(events.get(0) instanceof OrderCreatedEvent);
        OrderCreatedEvent orderEvent = (OrderCreatedEvent) events.get(0);
        assertEquals("e1", orderEvent.getEventId());
        assertEquals("ORD001", orderEvent.getOrderId());

        // Test second event
        assertTrue(events.get(1) instanceof PaymentReceivedEvent);
        PaymentReceivedEvent paymentEvent = (PaymentReceivedEvent) events.get(1);
        assertEquals("e2", paymentEvent.getEventId());
        assertEquals(100.0, paymentEvent.getAmountPaid(), 0.01);
    }

    @Test
    public void testReadInvalidJsonLines() throws IOException {
        // Write invalid JSON
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("{\"eventId\": \"e1\", \"invalidJson\": \n");
            writer.write("{\"eventId\": \"e2\", \"timestamp\": \"2025-07-29T10:15:00Z\", \"eventType\": \"PaymentReceived\", \"orderId\": \"ORD001\", \"amountPaid\": 100.00}\n");
        }

        List<Event> events = ingestionService.readEventsFromFile(testFile.getAbsolutePath());

        // Should only parse valid events
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof PaymentReceivedEvent);
    }

    @Test
    public void testReadEmptyFile() throws IOException {
        // Empty file
        List<Event> events = ingestionService.readEventsFromFile(testFile.getAbsolutePath());

        assertTrue("Empty file should return empty list", events.isEmpty());
    }

    @Test
    public void testReadNonExistentFile() {
        List<Event> events = ingestionService.readEventsFromFile("non-existent-file.json");

        assertTrue("Non-existent file should return empty list", events.isEmpty());
    }

    @Test
    public void testAllEventTypes() throws IOException {
        // Write all event types
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("{\"eventId\": \"e1\", \"timestamp\": \"2025-07-29T10:00:00Z\", \"eventType\": \"OrderCreated\", \"orderId\": \"ORD001\", \"customerId\": \"CUST001\", \"items\": [{\"itemId\": \"P001\", \"qty\": 1}], \"totalAmount\": 50.0}\n");
            writer.write("{\"eventId\": \"e2\", \"timestamp\": \"2025-07-29T10:05:00Z\", \"eventType\": \"PaymentReceived\", \"orderId\": \"ORD001\", \"amountPaid\": 50.0}\n");
            writer.write("{\"eventId\": \"e3\", \"timestamp\": \"2025-07-29T10:10:00Z\", \"eventType\": \"ShippingScheduled\", \"orderId\": \"ORD001\", \"shippingDate\": \"2025-07-30T09:00:00Z\"}\n");
            writer.write("{\"eventId\": \"e4\", \"timestamp\": \"2025-07-29T10:15:00Z\", \"eventType\": \"OrderCancelled\", \"orderId\": \"ORD002\", \"reason\": \"Test cancellation\"}\n");
        }

        List<Event> events = ingestionService.readEventsFromFile(testFile.getAbsolutePath());

        assertEquals(4, events.size());
        assertTrue(events.get(0) instanceof OrderCreatedEvent);
        assertTrue(events.get(1) instanceof PaymentReceivedEvent);
        assertTrue(events.get(2) instanceof ShippingScheduledEvent);
        assertTrue(events.get(3) instanceof OrderCancelledEvent);
    }

    @Test
    public void testUnknownEventType() throws IOException {
        // Write unknown event type and a known event type
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("{\"eventId\": \"e1\", \"timestamp\": \"2025-07-29T10:00:00Z\", \"eventType\": \"UnknownEvent\", \"orderId\": \"ORD001\"}\n");
            writer.write("{\"eventId\": \"e2\", \"timestamp\": \"2025-07-29T10:05:00Z\", \"eventType\": \"PaymentReceived\", \"orderId\": \"ORD001\", \"amountPaid\": 50.0}\n");
        }

        List<Event> events = ingestionService.readEventsFromFile(testFile.getAbsolutePath());

        // Should only parse known event types (unknown event filtered out)
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof PaymentReceivedEvent); // Changed from index 1 to 0
    }
}
