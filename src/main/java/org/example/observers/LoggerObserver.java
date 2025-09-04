package org.example.observers;

import org.example.domain.Order;
import org.example.events.Event;
import java.time.format.DateTimeFormatter;

/**
 * Observer that logs order status changes and event processing to the console.
 */
public class LoggerObserver implements OrderObserver {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs order status changes.
     */
    @Override
    public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        System.out.printf("[LOGGER] Order %s status changed from %s to %s%n",
                order.getOrderId(), previousStatus, newStatus);
    }

    /**
     * Logs processed events.
     */
    @Override
    public void onEventProcessed(Event event, Order order) {
        System.out.printf("[LOGGER] Event processed - Type: %s, EventId: %s, OrderId: %s at %s%n",
                event.getEventType(), event.getEventId(),
                order != null ? order.getOrderId() : "N/A",
                event.getTimestamp().format(FORMATTER));
    }
}
