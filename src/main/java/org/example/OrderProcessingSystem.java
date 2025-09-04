package org.example;

import org.example.events.Event;
import org.example.observers.AlertObserver;
import org.example.observers.LoggerObserver;
import org.example.processing.EventIngestionService;
import org.example.processing.EventProcessor;

import java.util.List;

/**
 * Entry point for the order processing system. Initializes components, ingests
 * events, and processes them.
 */
public class OrderProcessingSystem {

    /**
     * Main method to start the order processing system.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Order Processing Started\n");

        // Initialize components
        EventIngestionService ingestionService = new EventIngestionService();
        EventProcessor eventProcessor = new EventProcessor();

        // Register observers
        eventProcessor.addObserver(new LoggerObserver());
        eventProcessor.addObserver(new AlertObserver());

        // Process events from file
        String eventsFile = "src/main/resources/events.json";
        List<Event> events = ingestionService.readEventsFromFile(eventsFile);

        System.out.printf("Loaded %d events from %s%n%n", events.size(), eventsFile);

        // Process each event
        for (Event event : events) {
            System.out.printf("Processing event: %s%n", event.getEventId());
            eventProcessor.processEvent(event);
            System.out.println("=====");
        }

        // Display final order states
        System.out.println("\nFinal Order States");
        eventProcessor.getOrders().values().forEach(order -> {
            System.out.printf("Order: %s | Status: %s | Events: %d%n",
                    order.getOrderId(), order.getStatus(),
                    order.getEventHistory().size());
        });

        System.out.println("\nProcessing Complete");
    }
}
