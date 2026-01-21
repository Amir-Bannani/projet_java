package com.pharmacy.model;

public class Client {
    private int id;
    private String name;
    private String phone;
    private String email;
    private double totalPurchases;

    public Client() {}

    public Client(int id, String name, String phone, String email, double totalPurchases) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.totalPurchases = totalPurchases;
    }
    
    public Client(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(double totalPurchases) { this.totalPurchases = totalPurchases; }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
