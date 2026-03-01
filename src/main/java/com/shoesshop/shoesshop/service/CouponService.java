package com.shoesshop.shoesshop.service;

import com.shoesshop.shoesshop.entity.Coupon;
import com.shoesshop.shoesshop.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Page<Coupon> getCoupons(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return couponRepository.findAll(paging);
        } else {
            return couponRepository.findByCouponNameContainingIgnoreCaseOrCouponCodeContainingIgnoreCase(keyword, keyword, paging);
        }
    }

    // Lưu Coupon (Dùng chung cho cả Create và Update)
    public void saveCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }

    // Lấy 1 Coupon theo ID (Dùng để hiển thị lên form Update)
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    // Xóa Coupon
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }
}