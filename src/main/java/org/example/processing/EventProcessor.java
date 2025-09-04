package org.example.processing;

import org.example.domain.Order;
import org.example.domain.OrderStatus;
import org.example.events.*;
import org.example.observers.OrderObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes events and updates orders, notifying observers of changes.
 */
public class EventProcessor {
    private final Map<String, Order> orders;
    private final List<OrderObserver> observers;

    public EventProcessor() {
        this.orders = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to receive notifications.
     * 
     * @param observer the observer to add
     */
    public void addObserver(OrderObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Removes a registered observer.
     * 
     * @param observer the observer to remove
     */
    public void removeObserver(OrderObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Processes an event and updates the corresponding order.
     * Notifies observers of changes.
     * 
     * @param event the event to process
     */
    public void processEvent(Event event) {
        try {
            if (event instanceof OrderCreatedEvent) {
                processOrderCreatedEvent((OrderCreatedEvent) event);
            } else if (event instanceof PaymentReceivedEvent) {
                processPaymentReceivedEvent((PaymentReceivedEvent) event);
            } else if (event instanceof ShippingScheduledEvent) {
                processShippingScheduledEvent((ShippingScheduledEvent) event);
            } else if (event instanceof OrderCancelledEvent) {
                processOrderCancelledEvent((OrderCancelledEvent) event);
            } else {
                System.out.printf("Warning: Unsupported event type '%s' with eventId '%s'%n",
                        event.getEventType(), event.getEventId());
                return;
            }

            // Notify observers that an event was processed
            Order relatedOrder = getRelatedOrder(event);
            notifyEventProcessed(event, relatedOrder);

        } catch (Exception e) {
            System.err.printf("Error processing event %s: %s%n", event.getEventId(), e.getMessage());
        }
    }

    /**
     * Handles OrderCreatedEvent: creates a new order.
     */
    private void processOrderCreatedEvent(OrderCreatedEvent event) {
        Order order = new Order(event.getOrderId(), event.getCustomerId(),
                event.getItems(), event.getTotalAmount());
        order.addEventToHistory(event);
        orders.put(order.getOrderId(), order);

        System.out.printf("Created new order: %s%n", order);
    }

    /**
     * Handles PaymentReceivedEvent: updates order status to PAID or PARTIALLY_PAID.
     */
    private void processPaymentReceivedEvent(PaymentReceivedEvent event) {
        Order order = orders.get(event.getOrderId());
        if (order == null) {
            System.err.printf("Order not found for payment event: %s%n", event.getOrderId());
            return;
        }

        String previousStatus = order.getStatus().name();

        if (event.getAmountPaid() >= order.getTotalAmount()) {
            order.setStatus(OrderStatus.PAID);
        } else if (event.getAmountPaid() > 0) {
            order.setStatus(OrderStatus.PARTIALLY_PAID);
        }

        order.addEventToHistory(event);
        notifyStatusChanged(order, previousStatus, order.getStatus().name());

        System.out.printf("Payment processed for order %s: $%.2f (Status: %s)%n",
                order.getOrderId(), event.getAmountPaid(), order.getStatus());
    }

    /**
     * Handles ShippingScheduledEvent: updates order status to SHIPPED.
     */
    private void processShippingScheduledEvent(ShippingScheduledEvent event) {
        Order order = orders.get(event.getOrderId());
        if (order == null) {
            System.err.printf("Order not found for shipping event: %s%n", event.getOrderId());
            return;
        }

        String previousStatus = order.getStatus().name();
        order.setStatus(OrderStatus.SHIPPED);
        order.addEventToHistory(event);

        notifyStatusChanged(order, previousStatus, order.getStatus().name());

        System.out.printf("Shipping scheduled for order %s on %s%n",
                order.getOrderId(), event.getShippingDate());
    }

    /**
     * Handles OrderCancelledEvent: updates order status to CANCELLED.
     */
    private void processOrderCancelledEvent(OrderCancelledEvent event) {
        Order order = orders.get(event.getOrderId());
        if (order == null) {
            System.err.printf("Order not found for cancellation event: %s%n", event.getOrderId());
            return;
        }

        String previousStatus = order.getStatus().name();
        order.setStatus(OrderStatus.CANCELLED);
        order.addEventToHistory(event);

        notifyStatusChanged(order, previousStatus, order.getStatus().name());

        System.out.printf("Order %s cancelled. Reason: %s%n",
                order.getOrderId(), event.getReason());
    }

    /**
     * Gets the order related to the given event.
     * 
     * @param event the event
     * @return the related order, or null if not found
     */
    private Order getRelatedOrder(Event event) {
        if (event instanceof OrderCreatedEvent) {
            return orders.get(((OrderCreatedEvent) event).getOrderId());
        } else if (event instanceof PaymentReceivedEvent) {
            return orders.get(((PaymentReceivedEvent) event).getOrderId());
        } else if (event instanceof ShippingScheduledEvent) {
            return orders.get(((ShippingScheduledEvent) event).getOrderId());
        } else if (event instanceof OrderCancelledEvent) {
            return orders.get(((OrderCancelledEvent) event).getOrderId());
        }
        return null;
    }

    /**
     * Notifies observers of a status change.
     */
    private void notifyStatusChanged(Order order, String previousStatus, String newStatus) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(order, previousStatus, newStatus);
        }
    }

    /**
     * Notifies observers that an event was processed.
     */
    private void notifyEventProcessed(Event event, Order order) {
        for (OrderObserver observer : observers) {
            observer.onEventProcessed(event, order);
        }
    }

    /**
     * Returns a copy of the orders map.
     * 
     * @return map of orderId to Order
     */
    public Map<String, Order> getOrders() {
        return new HashMap<>(orders);
    }
}
