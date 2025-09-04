package org.example.events;

import java.time.LocalDateTime;

/**
 * Abstract base class for all order-related events.
 * Contains common event fields: eventId, timestamp, and eventType.
 */
public abstract class Event {
    protected String eventId;
    protected LocalDateTime timestamp;
    protected String eventType;

    public Event() {
    }

    public Event(String eventId, LocalDateTime timestamp, String eventType) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return String.format("Event{eventId='%s', eventType='%s', timestamp=%s}",
                eventId, eventType, timestamp);
    }
}
