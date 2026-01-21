package com.pharmacy.model;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private int minThreshold;
    private int supplierId;

    public Product() {}

    public Product(int id, String name, String description, double price, int stockQuantity, int minThreshold, int supplierId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minThreshold = minThreshold;
        this.supplierId = supplierId; // Foreign key
    }
    
    // Constructor without ID for new products
    public Product(String name, String description, double price, int stockQuantity, int minThreshold, int supplierId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minThreshold = minThreshold;
        this.supplierId = supplierId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public int getMinThreshold() { return minThreshold; }
    public void setMinThreshold(int minThreshold) { this.minThreshold = minThreshold; }
    
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    @Override
    public String toString() {
        return name + " (" + stockQuantity + ") - " + price + "€";
    }
}
