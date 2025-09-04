package org.example.processing;

import org.example.domain.OrderItem;
import org.example.events.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for ingesting events from a JSON file and parsing them into event
 * objects.
 */
public class EventIngestionService {
    private final ObjectMapper objectMapper;

    public EventIngestionService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Reads events from a JSON file - each line contains a separate JSON event.
     * Note: This hatchling implementation supports basic JSON parsing.
     * 
     * @param filePath the path to the events file
     * @return list of parsed Event objects
     */
    public List<Event> readEventsFromFile(String filePath) {
        List<Event> events = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        Event event = parseEventFromJson(line);
                        if (event != null) {
                            events.add(event);
                        }
                    } catch (Exception e) {
                        System.err.printf("Error parsing event from line: %s - %s%n", line, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.printf("Error reading file %s: %s%n", filePath, e.getMessage());
        }

        return events;
    }

    /**
     * Parses a JSON string into the appropriate Event subclass.
     * 
     * @param jsonLine the JSON string
     * @return the parsed Event, or null if unknown type
     * @throws Exception if parsing fails
     */
    private Event parseEventFromJson(String jsonLine) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonLine);

        String eventId = rootNode.get("eventId").asText();
        String eventType = rootNode.get("eventType").asText();
        LocalDateTime timestamp = LocalDateTime.parse(rootNode.get("timestamp").asText(),
                DateTimeFormatter.ISO_DATE_TIME);

        switch (eventType) {
            case "OrderCreated":
                return parseOrderCreatedEvent(rootNode, eventId, timestamp);
            case "PaymentReceived":
                return parsePaymentReceivedEvent(rootNode, eventId, timestamp);
            case "ShippingScheduled":
                return parseShippingScheduledEvent(rootNode, eventId, timestamp);
            case "OrderCancelled":
                return parseOrderCancelledEvent(rootNode, eventId, timestamp);
            default:
                System.out.printf("Warning: Unknown event type '%s' for eventId '%s'%n", eventType, eventId);
                return null;
        }
    }

    /**
     * Parses an OrderCreatedEvent from JSON.
     */
    private OrderCreatedEvent parseOrderCreatedEvent(JsonNode node, String eventId, LocalDateTime timestamp) {
        String orderId = node.get("orderId").asText();
        String customerId = node.get("customerId").asText();
        double totalAmount = node.get("totalAmount").asDouble();

        List<OrderItem> items = new ArrayList<>();
        JsonNode itemsNode = node.get("items");
        if (itemsNode.isArray()) {
            for (JsonNode itemNode : itemsNode) {
                String itemId = itemNode.get("itemId").asText();
                int qty = itemNode.get("qty").asInt();
                items.add(new OrderItem(itemId, qty));
            }
        }

        return new OrderCreatedEvent(eventId, timestamp, orderId, customerId, items, totalAmount);
    }

    /**
     * Parses a PaymentReceivedEvent from JSON.
     */
    private PaymentReceivedEvent parsePaymentReceivedEvent(JsonNode node, String eventId, LocalDateTime timestamp) {
        String orderId = node.get("orderId").asText();
        double amountPaid = node.get("amountPaid").asDouble();
        return new PaymentReceivedEvent(eventId, timestamp, orderId, amountPaid);
    }

    /**
     * Parses a ShippingScheduledEvent from JSON.
     */
    private ShippingScheduledEvent parseShippingScheduledEvent(JsonNode node, String eventId, LocalDateTime timestamp) {
        String orderId = node.get("orderId").asText();
        LocalDateTime shippingDate = LocalDateTime.parse(node.get("shippingDate").asText(),
                DateTimeFormatter.ISO_DATE_TIME);
        return new ShippingScheduledEvent(eventId, timestamp, orderId, shippingDate);
    }

    /**
     * Parses an OrderCancelledEvent from JSON.
     */
    private OrderCancelledEvent parseOrderCancelledEvent(JsonNode node, String eventId, LocalDateTime timestamp) {
        String orderId = node.get("orderId").asText();
        String reason = node.get("reason").asText();
        return new OrderCancelledEvent(eventId, timestamp, orderId, reason);
    }
}
