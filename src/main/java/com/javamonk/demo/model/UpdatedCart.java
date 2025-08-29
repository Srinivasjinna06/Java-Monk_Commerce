package com.javamonk.demo.model;

import java.util.List;
import lombok.Data;

@Data
public class UpdatedCart {
    private List<CartItem> items;
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;
    
}