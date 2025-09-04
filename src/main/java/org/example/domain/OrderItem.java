package org.example.domain;

/**
 * Represents an item in an order, including item ID and quantity.
 */
public class OrderItem {
    private String itemId;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("OrderItem{itemId='%s', quantity=%d}", itemId, quantity);
    }
}
