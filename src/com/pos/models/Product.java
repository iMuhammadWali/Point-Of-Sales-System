package com.pos.models;

public class Product {
    private int productID;
    private String supplierName;
    private String barcode;
    private String name;
    private String description;
    private double price;
    private double costPrice;
    private int currentStock;
    private String createdAt;

    public Product(int productID, String supplierName, String barcode, String name, String description,
                   double price, double costPrice, int currentStock, String createdAt) {
        this.productID = productID;
        this.supplierName = supplierName;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.costPrice = costPrice;
        this.currentStock = currentStock;
        this.createdAt = createdAt;
    }

    // Getters
    public int getProductID() {
        return productID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void printProduct() {
        System.out.println("Product ID: " + productID);
        System.out.println("Supplier Name: " + supplierName);
        System.out.println("Barcode: " + barcode);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Price: " + price);
        System.out.println("Cost Price: " + costPrice);
        System.out.println("Current Stock: " + currentStock);
        System.out.println("Created At: " + createdAt);
    }
}