package com.shoesshop.shoesshop.repository;

import com.shoesshop.shoesshop.entity.Coupon; // <-- Thay bằng đường dẫn chuẩn
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    // Tìm kiếm có phân trang (trả về Page thay vì List)
    Page<Coupon> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code, Pageable pageable);
}