package com.pos.model;

import java.util.List;

public class Customer {
    private String name;
    private String phone;
    private String email;
    private List<String> purchaseHistory;

    public Customer(String name, String phone, String email, List<String> purchaseHistory) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.purchaseHistory = purchaseHistory;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public List<String> getPurchaseHistory() { return purchaseHistory; }
}
