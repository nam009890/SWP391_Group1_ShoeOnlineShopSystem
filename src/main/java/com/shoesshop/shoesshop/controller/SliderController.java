package com.shoesshop.shoesshop.controller;

import com.shoesshop.shoesshop.entity.Slider;
import com.shoesshop.shoesshop.service.SliderService;
import com.shoesshop.shoesshop.repository.CouponRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SliderController {

    @Autowired
    private SliderService sliderService;

    @Autowired
    private CouponRepository couponRepository; 

    @GetMapping("/sliders")
    public String listSliders(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Slider> pageSliders = sliderService.getSliders(keyword, page, size);
        model.addAttribute("sliders", pageSliders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSliders.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "slider-list";
    }

    @GetMapping("/sliders/create")
    public String showCreateSliderForm(Model model) {
        model.addAttribute("slider", new Slider());
        
        // Đổ dữ liệu giả lập cho 2 Popup
        model.addAttribute("coupons", couponRepository.findAll());
        List<Map<String, Object>> mockProducts = new ArrayList<>();
        mockProducts.add(Map.of("id", 1, "name", "Adidas predator", "price", 1000000));
        mockProducts.add(Map.of("id", 2, "name", "Nike Mecury", "price", 950000));
        model.addAttribute("products", mockProducts);

        return "slider-create";
    }

    // DUY NHẤT 1 HÀM SAVE Ở ĐÂY (Đã bao gồm Validation chặn lỗi)
    @PostMapping("/sliders/save")
    public String saveSlider(@Valid @ModelAttribute("slider") Slider slider, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Load lại dữ liệu cho Popup nếu nhập lỗi
            model.addAttribute("coupons", couponRepository.findAll());
            List<Map<String, Object>> mockProducts = new ArrayList<>();
            mockProducts.add(Map.of("id", 1, "name", "Adidas predator", "price", 1000000));
            mockProducts.add(Map.of("id", 2, "name", "Nike Mecury", "price", 950000));
            model.addAttribute("products", mockProducts);
            
            return slider.getId() == null ? "slider-create" : "slider-update"; 
        }
        
        sliderService.saveSlider(slider);
        return "redirect:/sliders";
    }

    @GetMapping("/sliders/update/{id}")
    public String showUpdateSliderForm(@PathVariable Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        if (slider == null) {
            return "redirect:/sliders";
        }
        model.addAttribute("slider", slider);
        
        model.addAttribute("coupons", couponRepository.findAll());
        List<Map<String, Object>> mockProducts = new ArrayList<>();
        mockProducts.add(Map.of("id", 1, "name", "Adidas predator", "price", 1000000));
        mockProducts.add(Map.of("id", 2, "name", "Nike Mecury", "price", 950000));
        model.addAttribute("products", mockProducts);

        return "slider-update";
    }

    @GetMapping("/sliders/delete/{id}")
    public String deleteSlider(@PathVariable Long id) {
        sliderService.deleteSlider(id);
        return "redirect:/sliders";
    }
}