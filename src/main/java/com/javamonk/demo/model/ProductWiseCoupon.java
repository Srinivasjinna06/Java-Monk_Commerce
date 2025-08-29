package com.javamonk.demo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@DiscriminatorValue("PRODUCT_WISE")
public class ProductWiseCoupon extends Coupon {
    private Long productId;
    private double discountPercentage;

    @Override
    public double calculateDiscount(Cart cart) {
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .mapToDouble(item -> item.getPrice() * item.getQuantity() * (discountPercentage / 100))
                .sum();
    }

    @Override
    public boolean isApplicable(Cart cart) {
        return cart.getItems().stream().anyMatch(item -> item.getProductId().equals(productId)) &&
                (getExpirationDate() == null || getExpirationDate().isAfter(LocalDateTime.now()));
    }
}