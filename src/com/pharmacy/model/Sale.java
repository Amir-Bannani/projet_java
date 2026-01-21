package com.pharmacy.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int id;
    private int clientId; // 0 if null/anonymous
    private int userId;
    private Timestamp saleDate;
    private double totalAmount;
    private String clientName; // Transient, for display
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {}

    public Sale(int id, int clientId, int userId, Timestamp saleDate, double totalAmount) {
        this.id = id;
        this.clientId = clientId;
        this.userId = userId;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
    public void addItem(SaleItem item) { this.items.add(item); }
}
