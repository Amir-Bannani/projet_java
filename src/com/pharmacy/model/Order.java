package com.pharmacy.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int supplierId;
    private Timestamp orderDate;
    private String status; // PENDING, RECEIVED, CANCELLED
    private double totalAmount;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(int id, int supplierId, Timestamp orderDate, String status, double totalAmount) {
        this.id = id;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void addItem(OrderItem item) { this.items.add(item); }
}
