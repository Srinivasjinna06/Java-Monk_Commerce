package com.javamonk.demo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@DiscriminatorValue("CART_WISE")
public class CartWiseCoupon extends Coupon {
    private double threshold;
    private double discountPercentage;

    @Override
    public double calculateDiscount(Cart cart) {
        double total = cart.getTotalPrice();
        if (total > threshold) {
            return total * (discountPercentage / 100);
        }
        return 0;
    }

    @Override
    public boolean isApplicable(Cart cart) {
        return cart.getTotalPrice() > threshold &&
                (getExpirationDate() == null || getExpirationDate().isAfter(LocalDateTime.now()));
    }
}