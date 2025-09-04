package org.example.observers;

import org.example.domain.Order;
import org.example.events.Event;

/**
 * Observer interface for receiving notifications about order status changes and
 * event processing.
 */
public interface OrderObserver {
    /**
     * Called when an order's status changes.
     * 
     * @param order          the order whose status changed
     * @param previousStatus the previous status
     * @param newStatus      the new status
     */
    void onOrderStatusChanged(Order order, String previousStatus, String newStatus);

    /**
     * Called when an event is processed for an order.
     * 
     * @param event the processed event
     * @param order the related order (may be null)
     */
    void onEventProcessed(Event event, Order order);
}
