package com.javamonk.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicableCoupon {
    private Long couponId;
    private String type;
    private double discount;
}