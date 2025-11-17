package com.pos.model;

import javafx.beans.property.*;

public class Product {
    private final StringProperty name;
    private final IntegerProperty quantity;
    private final DoubleProperty price;
    private final DoubleProperty discount;

    public Product(String name, int quantity, double price, double discount) {
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.discount = new SimpleDoubleProperty(discount);
    }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }
    public DoubleProperty priceProperty() { return price; }

    public double getDiscount() { return discount.get(); }
    public void setDiscount(double discount) { this.discount.set(discount); }
    public DoubleProperty discountProperty() { return discount; }
}
