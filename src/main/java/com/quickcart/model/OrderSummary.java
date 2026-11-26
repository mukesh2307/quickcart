package com.quickcart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderSummary {
    private final long id;
    private final String customerName;
    private final String deliveryAddress;
    private final BigDecimal totalAmount;
    private final String status;
    private final Timestamp createdAt;

    public OrderSummary(long id, String customerName, String deliveryAddress, BigDecimal totalAmount,
                        String status, Timestamp createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}
