package org.example.events;

import java.time.LocalDateTime;

/**
 * Event representing the scheduling of shipping for an order.
 */
public class ShippingScheduledEvent extends Event {
    private String orderId;
    private LocalDateTime shippingDate;

    public ShippingScheduledEvent() {
        super();
    }

    public ShippingScheduledEvent(String eventId, LocalDateTime timestamp, String orderId, LocalDateTime shippingDate) {
        super(eventId, timestamp, "ShippingScheduled");
        this.orderId = orderId;
        this.shippingDate = shippingDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
    }
}
