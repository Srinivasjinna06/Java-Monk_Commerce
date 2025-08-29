package com.javamonk.demo.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "coupon_type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "coupon_type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CartWiseCoupon.class, name = "CART_WISE"),
    @JsonSubTypes.Type(value = ProductWiseCoupon.class, name = "PRODUCT_WISE"),
    @JsonSubTypes.Type(value = BxGyCoupon.class, name = "BXGY")
})
public abstract class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime expirationDate;

    public abstract double calculateDiscount(Cart cart);
    public abstract boolean isApplicable(Cart cart);
}