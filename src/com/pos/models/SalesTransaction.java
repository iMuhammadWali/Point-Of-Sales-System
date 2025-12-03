package com.pos.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SalesTransaction {
    private Integer saleID;
    private Integer customerID;
    private Integer cashierID;
    private Integer shiftID;
    private BigDecimal totalAmount;      // Before any processing

    private BigDecimal taxAmount;
    private BigDecimal discountAmount;   // Global discount amount

    private BigDecimal finalAmount;      // After tax and discounts

    private String status;
    private LocalDateTime createdAt;

    private List<SaleItem> items;
    private List<Payment> payments;

    public SalesTransaction() {}

    public SalesTransaction(Integer customerID, Integer cashierID, Integer shiftID,
                            BigDecimal totalAmount, BigDecimal taxAmount,
                            BigDecimal discountAmount, BigDecimal finalAmount, String status) {
        this.customerID = customerID;
        this.cashierID = cashierID;
        this.shiftID = shiftID;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getSaleID() { return saleID; }
    public void setSaleID(Integer saleID) { this.saleID = saleID; }

    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }

    public Integer getCashierID() { return cashierID; }
    public void setCashierID(Integer cashierID) { this.cashierID = cashierID; }

    public Integer getShiftID() { return shiftID; }
    public void setShiftID(Integer shiftID) { this.shiftID = shiftID; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}

