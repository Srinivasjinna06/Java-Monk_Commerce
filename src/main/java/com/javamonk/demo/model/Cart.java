package com.javamonk.demo.model;

import lombok.Data;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
public class Cart {
    private List<CartItem> items = Collections.emptyList();

    public double getTotalPrice() {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public CartItem findItemByProductId(Long productId) {
        Optional<CartItem> optionalItem = items.stream().filter(item -> item.getProductId().equals(productId))
                .findFirst();
        return optionalItem.orElse(null);
    }
}