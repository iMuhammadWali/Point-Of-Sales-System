package com.pos.models;

import java.math.BigDecimal;

public class SaleItem {
    private Integer saleItemID;
    private Integer saleId;
    private Integer productID;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    // the two Constructors.
    public SaleItem() {}
    public SaleItem(Integer productID, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    // Getters and Setters
    public Integer getSaleItemID() { return saleItemID; }
    public void setSaleItemID(Integer saleItemID) { this.saleItemID = saleItemID; }

    public Integer getSaleId() { return saleId; }
    public void setSaleId(Integer saleId) { this.saleId = saleId; }

    public Integer getProductID() { return productID; }
    public void setProductID(Integer productID) { this.productID = productID; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}