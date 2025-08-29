package com.javamonk.demo.model;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItem {
    private Long productId;
    private int quantity;
    @Positive(message = "Price must be positive")
    private double price;
    private double totalDiscount = 0;
}