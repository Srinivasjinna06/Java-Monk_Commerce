package com.javamonk.demo.controller;

import com.javamonk.demo.model.*;
import com.javamonk.demo.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController

@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService service;

    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return service.createCoupon(coupon);
    }

    @GetMapping
    public List<Coupon> getAllCoupons() {
        return service.getAllCoupons();
    }

    @GetMapping("/{id}")
    public Coupon getCouponById(@PathVariable Long id) {
        return service.getCouponById(id);
    }

    @PutMapping("/{id}")
    public Coupon updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return service.updateCoupon(id, coupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        service.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/applicable-coupons")
    public List<ApplicableCoupon> getApplicableCoupons(@RequestBody Cart cart) {
        return service.getApplicableCoupons(cart);
    }

    @PostMapping("/apply-coupon/{id}")
    public UpdatedCart applyCoupon(@PathVariable Long id, @RequestBody @Valid Cart cart) {
        if (cart == null || cart.getItems() == null) {
            throw new IllegalArgumentException("Cart or items cannot be null");
        }
        return service.applyCoupon(id, cart);
    }

    @PostMapping("/bulk")
    public List<Coupon> createBulkCoupons(@RequestBody List<Coupon> coupons) {
        return service.createBulkCoupons(coupons);
    }
}