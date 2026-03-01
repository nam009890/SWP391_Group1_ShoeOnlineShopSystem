package com.shoesshop.shoesshop.service;

import com.shoesshop.shoesshop.entity.Slider;
import com.shoesshop.shoesshop.entity.Coupon;
import com.shoesshop.shoesshop.repository.SliderRepository;
import com.shoesshop.shoesshop.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.shoesshop.shoesshop.repository.ProductRepository;

import java.util.List;

@Service
public class SliderService {

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private CouponRepository couponRepository;

    public Page<Slider> getSliders(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return sliderRepository.findAll(paging);
        } else {
            return sliderRepository.findBySliderTitleContainingIgnoreCase(keyword, paging);
        }
    }

    // Đã thêm tham số List<Long> couponIds
    @Autowired
    private com.shoesshop.shoesshop.repository.SliderRepository sliderRepository;

    @Autowired
    private com.shoesshop.shoesshop.repository.CouponRepository couponRepository;

    @Autowired
    private com.shoesshop.shoesshop.repository.ProductRepository productRepository; // Thêm dòng này

    // Sửa lại hàm saveSlider để nhận thêm 2 mảng ID
    public void saveSlider(Slider slider, java.util.List<Long> couponIds, java.util.List<Long> productIds) {
        if (slider.getCreatedAt() == null) {
            slider.setCreatedAt(java.time.LocalDateTime.now());
        }
        slider.setUpdatedAt(java.time.LocalDateTime.now());

        // Xử lý lưu danh sách Coupon
        if (couponIds != null && !couponIds.isEmpty()) {
            slider.setCoupons(couponRepository.findAllById(couponIds));
        } else {
            slider.setCoupons(new java.util.ArrayList<>());
        }

        // Xử lý lưu danh sách Product
        if (productIds != null && !productIds.isEmpty()) {
            slider.setProducts(productRepository.findAllById(productIds));
        } else {
            slider.setProducts(new java.util.ArrayList<>());
        }

        sliderRepository.save(slider);
    }

    public Slider getSliderById(Long id) {
        return sliderRepository.findById(id).orElse(null);
    }

    public void deleteSlider(Long id) {
        sliderRepository.deleteById(id);
    }
}