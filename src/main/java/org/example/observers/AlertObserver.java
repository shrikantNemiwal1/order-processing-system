package org.example.observers;

import org.example.domain.Order;
import org.example.events.Event;

/**
 * Observer that prints alerts for critical order status changes and
 * cancellations.
 */
public class AlertObserver implements OrderObserver {

    /**
     * Prints an alert if the new status is critical (CANCELLED or SHIPPED).
     */
    @Override
    public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        if (isCriticalStatusChange(newStatus)) {
            System.out.printf("[ALERT] Sending alert for Order %s: Status changed to %s%n",
                    order.getOrderId(), newStatus);
        }
    }

    /**
     * Prints an alert for critical events, such as order cancellation.
     */
    @Override
    public void onEventProcessed(Event event, Order order) {
        if ("OrderCancelled".equals(event.getEventType())) {
            System.out.printf("[ALERT] Critical event: Order %s has been cancelled%n",
                    order != null ? order.getOrderId() : "Unknown");
        }
    }

    /**
     * Determines if a status change is critical.
     * 
     * @param status the status to check
     * @return true if status is CANCELLED or SHIPPED
     */
    private boolean isCriticalStatusChange(String status) {
        return "CANCELLED".equals(status) || "SHIPPED".equals(status);
    }
}
