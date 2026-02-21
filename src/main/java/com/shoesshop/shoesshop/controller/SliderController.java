package com.shoesshop.shoesshop.controller;

import com.shoesshop.shoesshop.entity.Slider;
import com.shoesshop.shoesshop.repository.SliderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;

@Controller
public class SliderController {

    @Autowired
    private SliderRepository sliderRepository;

    // Khởi tạo dữ liệu mẫu (Mock data)
    public SliderController(SliderRepository repo) {
        this.sliderRepository = repo;
        if (repo.count() == 0) {
            repo.save(new Slider(null, "Super sale 1/1", "", LocalDate.of(2024, 10, 25)));
            repo.save(new Slider(null, "Super sale 2/2", "", LocalDate.of(2024, 10, 25)));
            repo.save(new Slider(null, "Super sale 3/3", "", LocalDate.of(2024, 10, 25)));
            repo.save(new Slider(null, "Black Friday", "", LocalDate.of(2024, 10, 25)));
            repo.save(new Slider(null, "End Year sale", "", LocalDate.of(2024, 10, 25)));
            
            // Tạo thêm data để test phân trang
            for (int i = 6; i <= 15; i++) {
                repo.save(new Slider(null, "Slider Test " + i, "Mô tả " + i, LocalDate.now()));
            }
        }
    }

    @GetMapping("/sliders")
    public String listSliders(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Slider> pageSliders;

        if (keyword == null || keyword.isEmpty()) {
            pageSliders = sliderRepository.findAll(paging);
        } else {
            pageSliders = sliderRepository.findByNameContainingIgnoreCase(keyword, paging);
        }

        model.addAttribute("sliders", pageSliders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSliders.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "slider-list";
    }
    @Autowired
    private com.shoesshop.shoesshop.repository.CouponRepository couponRepository; // Nhớ import CouponRepository lên đầu file nhé

    // Hiển thị trang Create Slider
    @GetMapping("/sliders/create")
    public String showCreateSliderForm(Model model) {
        
        // 1. Lấy danh sách Coupon từ Database để đổ vào Popup "Add Coupon Code"
        model.addAttribute("coupons", couponRepository.findAll());
        
        // 2. Tạo một vài dữ liệu Sản phẩm giả (Mock data) để đổ vào Popup "Add Product"
        // (Sau này bạn có bảng Product thì thay bằng productRepository.findAll() nhé)
        java.util.List<java.util.Map<String, Object>> mockProducts = new java.util.ArrayList<>();
        mockProducts.add(java.util.Map.of("id", 1, "name", "Adidas predator", "price", 1000000));
        mockProducts.add(java.util.Map.of("id", 2, "name", "Nike Mecury", "price", 950000));
        
        model.addAttribute("products", mockProducts);
        
        return "slider-create"; // Trả về file HTML giao diện tạo mới
    }
    // 1. Chức năng Xóa (Delete)
    @GetMapping("/sliders/delete/{id}")
    public String deleteSlider(@PathVariable Long id) {
        sliderRepository.deleteById(id);
        return "redirect:/sliders"; // Xóa xong tự động quay về trang danh sách
    }

    // 2. Hiển thị trang Cập nhật (Update)
    @GetMapping("/sliders/update/{id}")
    public String showUpdateSliderForm(@PathVariable Long id, Model model) {
        // Tìm slider theo ID, nếu không thấy thì quay về trang chủ
        Slider slider = sliderRepository.findById(id).orElse(null);
        if (slider == null) {
            return "redirect:/sliders";
        }
        model.addAttribute("slider", slider); // Đẩy dữ liệu cũ sang giao diện
        
        // Đổ lại dữ liệu cho 2 cái Popup (y hệt bên trang Create)
        model.addAttribute("coupons", couponRepository.findAll());
        java.util.List<java.util.Map<String, Object>> mockProducts = new java.util.ArrayList<>();
        mockProducts.add(java.util.Map.of("id", 1, "name", "Adidas predator", "price", 1000000));
        mockProducts.add(java.util.Map.of("id", 2, "name", "Nike Mecury", "price", 950000));
        model.addAttribute("products", mockProducts);
        
        return "slider-update"; // Trả về file giao diện update
    }
}