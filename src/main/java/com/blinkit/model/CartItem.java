package com.blinkit.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {
    private final int productId;
    private final String name, imageUrl;
    private final BigDecimal price;
    private int quantity;

    public CartItem(int productId, String name, BigDecimal price, String imageUrl, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
