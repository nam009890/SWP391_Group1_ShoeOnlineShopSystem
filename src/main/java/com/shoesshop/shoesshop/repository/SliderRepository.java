package com.shoesshop.shoesshop.repository;

import com.shoesshop.shoesshop.entity.Slider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SliderRepository extends JpaRepository<Slider, Long> {
    // Hàm tìm kiếm theo tên (không phân biệt hoa/thường)
    Page<Slider> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}