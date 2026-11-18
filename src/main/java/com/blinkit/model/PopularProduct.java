package com.blinkit.model;

import java.math.BigDecimal;

public class PopularProduct {
    private final int productId;
    private final String name;
    private final String categoryName;
    private final BigDecimal price;
    private final int stock;
    private final long quantitySold;
    private final long orderCount;

    public PopularProduct(int productId, String name, String categoryName, BigDecimal price,
                          int stock, long quantitySold, long orderCount) {
        this.productId = productId;
        this.name = name;
        this.categoryName = categoryName;
        this.price = price;
        this.stock = stock;
        this.quantitySold = quantitySold;
        this.orderCount = orderCount;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public long getQuantitySold() { return quantitySold; }
    public long getOrderCount() { return orderCount; }
}
