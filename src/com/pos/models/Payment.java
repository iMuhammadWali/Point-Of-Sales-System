package com.pos.models;

// Payment.java
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Integer paymentID;
    private Integer saleId;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public Payment() {}

    public Payment(String paymentMethod, BigDecimal amount, String status) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getPaymentID() { return paymentID; }
    public void setPaymentID(Integer paymentID) { this.paymentID = paymentID; }

    public Integer getSaleId() { return saleId; }
    public void setSaleId(Integer saleId) { this.saleId = saleId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}