package com.javamonk.demo.service;

import com.javamonk.demo.model.BxGyCoupon;
import com.javamonk.demo.model.Cart;
import com.javamonk.demo.model.CartItem;
import com.javamonk.demo.model.Coupon;
import com.javamonk.demo.model.ApplicableCoupon;
import com.javamonk.demo.model.CartWiseCoupon;
import com.javamonk.demo.model.ProductWiseCoupon;
import com.javamonk.demo.model.UpdatedCart;
import com.javamonk.demo.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private CouponRepository repository;

    public Coupon createCoupon(Coupon coupon) {
        return repository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return repository.findAll();
    }

    public Coupon getCouponById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
        Coupon existing = getCouponById(id);
        existing.setName(updatedCoupon.getName());
        existing.setExpirationDate(updatedCoupon.getExpirationDate());

        if (existing.getClass() != updatedCoupon.getClass()) {
            throw new IllegalArgumentException("Coupon type cannot be changed");
        }

        if (existing instanceof CartWiseCoupon && updatedCoupon instanceof CartWiseCoupon) {
            CartWiseCoupon existingCW = (CartWiseCoupon) existing;
            CartWiseCoupon updatedCW = (CartWiseCoupon) updatedCoupon;
            existingCW.setThreshold(updatedCW.getThreshold());
            existingCW.setDiscountPercentage(updatedCW.getDiscountPercentage());
        } else if (existing instanceof ProductWiseCoupon && updatedCoupon instanceof ProductWiseCoupon) {
            ProductWiseCoupon existingPW = (ProductWiseCoupon) existing;
            ProductWiseCoupon updatedPW = (ProductWiseCoupon) updatedCoupon;
            existingPW.setProductId(updatedPW.getProductId());
            existingPW.setDiscountPercentage(updatedPW.getDiscountPercentage());
        } else if (existing instanceof BxGyCoupon && updatedCoupon instanceof BxGyCoupon) {
            BxGyCoupon existingBX = (BxGyCoupon) existing;
            BxGyCoupon updatedBX = (BxGyCoupon) updatedCoupon;
            existingBX.setBuyProducts(updatedBX.getBuyProducts());
            existingBX.setGetProducts(updatedBX.getGetProducts());
            existingBX.setRepetitionLimit(updatedBX.getRepetitionLimit());
        }

        return repository.save(existing);
    }

    public void deleteCoupon(Long id) {
        repository.deleteById(id);
    }

    public List<ApplicableCoupon> getApplicableCoupons(Cart cart) {
        return repository.findAll().stream()
                .filter(coupon -> coupon.isApplicable(cart))
                .map(coupon -> new ApplicableCoupon(
                        coupon.getId(),
                        coupon.getClass().getSimpleName().replace("Coupon", "").toLowerCase(),
                        coupon.calculateDiscount(cart)))
                .collect(Collectors.toList());
    }

    public UpdatedCart applyCoupon(Long id, Cart cart) {
        Coupon coupon = getCouponById(id);
        if (!coupon.isApplicable(cart)) {
            throw new RuntimeException("Coupon not applicable to this cart");
        }
        double discount = coupon.calculateDiscount(cart);

        if (coupon instanceof CartWiseCoupon cw) {
            double perc = cw.getDiscountPercentage() / 100;
            cart.getItems().forEach(item -> item.setTotalDiscount(item.getPrice() * item.getQuantity() * perc));
        } else if (coupon instanceof ProductWiseCoupon pw) {
            Long prodId = pw.getProductId();
            cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(prodId))
                    .forEach(item -> item.setTotalDiscount(
                            item.getPrice() * item.getQuantity() * (pw.getDiscountPercentage() / 100)));
        } else if (coupon instanceof BxGyCoupon bxgy) {
            int apps = bxgy.calculateApplications(cart);
            for (BxGyCoupon.ProductQuantity get : bxgy.getGetProducts()) {
                CartItem item = cart.findItemByProductId(get.getProductId());
                if (item != null) {
                    int freeQty = get.getQuantity() * apps;
                    item.setQuantity(item.getQuantity() + freeQty);
                    item.setTotalDiscount(item.getPrice() * freeQty);
                }
            }
        }

        UpdatedCart updated = new UpdatedCart();
        updated.setItems(cart.getItems());
        updated.setTotalPrice(cart.getTotalPrice());
        updated.setTotalDiscount(discount);
        updated.setFinalPrice(updated.getTotalPrice() - discount);
        return updated;
    }

    public List<Coupon> createBulkCoupons(List<Coupon> coupons) {
        if (coupons == null || coupons.isEmpty()) {
            throw new IllegalArgumentException("Coupon list cannot be empty");
        }
        return repository.saveAll(coupons);
    }

}