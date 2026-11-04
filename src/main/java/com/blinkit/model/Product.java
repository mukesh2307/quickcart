package com.blinkit.model;

import java.math.BigDecimal;

public class Product {
    private final int id, categoryId, stock;
    private final String categoryName, name, description, imageUrl;
    private final BigDecimal price;

    public Product(int id, int categoryId, String categoryName, String name,
                   String description, BigDecimal price, int stock, String imageUrl) {
        this.id = id; this.categoryId = categoryId; this.categoryName = categoryName;
        this.name = name; this.description = description; this.price = price;
        this.stock = stock; this.imageUrl = imageUrl;
    }
    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public int getStock() { return stock; }
    public String getCategoryName() { return categoryName; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public BigDecimal getPrice() { return price; }
}
