package com.pharmacy.model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double unitCost;
    // Transient field for display
    private String productName; 

    public OrderItem() {}

    public OrderItem(int id, int orderId, int productId, int quantity, double unitCost) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }
    
    public OrderItem(int productId, int quantity, double unitCost) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitCost() { return unitCost; }
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
