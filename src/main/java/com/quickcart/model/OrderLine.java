package com.quickcart.model;

import java.math.BigDecimal;

public class OrderLine {
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderLine(String productName, int quantity, BigDecimal unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
