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

    // Tự động tạo dữ liệu mẫu khi bật Server
    @PostConstruct
    public void initMockData() {
        if (couponRepository.count() == 0) {
            for (int i = 1; i <= 15; i++) {
                int safeDiscount = Math.min(i * 5, 50); // Khống chế tối đa 50%
                couponRepository.save(new Coupon(null, "Super Sale " + i + "/" + i, "CODE" + i, safeDiscount, LocalDate.now(), LocalDate.now().plusDays(30), true));
            }
        }
    }

    // Lấy danh sách (Có tìm kiếm và phân trang)
    public Page<Coupon> getCoupons(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return couponRepository.findAll(paging);
        } else {
            return couponRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword, paging);
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