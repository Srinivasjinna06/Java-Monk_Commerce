package com.javamonk.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@DiscriminatorValue("BXGY")
public class BxGyCoupon extends Coupon {
    @ElementCollection
    public List<ProductQuantity> buyProducts;
    @ElementCollection
    public List<ProductQuantity> getProducts;
    public int repetitionLimit;

    @Override
    public double calculateDiscount(Cart cart) {
        int applications = calculateApplications(cart);
        double discount = 0;
        for (ProductQuantity get : getProducts) {
            CartItem item = cart.findItemByProductId(get.getProductId());
            if (item != null) {
                int freeQty = get.getQuantity() * applications;
                discount += item.getPrice() * freeQty;
            }
        }
        return discount;
    }

@Override
public boolean isApplicable(Cart cart) {
    return calculateApplications(cart) > 0 &&
           (getExpirationDate() == null || getExpirationDate().isAfter(LocalDateTime.now()));
}

public int calculateApplications(Cart cart) {
    if (buyProducts.isEmpty() || getProducts.isEmpty()) return 0;
    int minBuyApps = Integer.MAX_VALUE;
    for (ProductQuantity buy : buyProducts) {
        CartItem item = cart.findItemByProductId(buy.getProductId());
        if (item == null || item.getQuantity() < buy.getQuantity()) return 0;
        minBuyApps = Math.min(minBuyApps, item.getQuantity() / buy.getQuantity());
    }
    int minGetApps = Integer.MAX_VALUE;
    for (ProductQuantity get : getProducts) {
        CartItem item = cart.findItemByProductId(get.getProductId());
        if (item != null) {
            minGetApps = Math.min(minGetApps, item.getQuantity() / get.getQuantity());
        } else {
            minGetApps = 0;
        }
    }
    return Math.min(Math.min(minBuyApps, minGetApps), repetitionLimit);
}

    @Embeddable
    @Data
    public static class ProductQuantity {
        public Long productId;
        public int quantity;
    }
}