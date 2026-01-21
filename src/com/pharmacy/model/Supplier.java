package com.pharmacy.model;

public class Supplier {
    private int id;
    private String name;
    private String contactInfo;
    private String address;

    public Supplier() {}

    public Supplier(int id, String name, String contactInfo, String address) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
    }
    
    public Supplier(String name, String contactInfo, String address) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return name;
    }
}
