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
import com.shoesshop.shoesshop.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SliderController {

    @Autowired
    private SliderService sliderService;

    @Autowired
    private CouponRepository couponRepository;
    @Autowired 
    private ProductRepository productRepository;

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

    @PostMapping("/sliders/save")
    public String saveSlider(
            @Valid @ModelAttribute("slider") Slider slider, 
            BindingResult result, 
            @RequestParam(value = "couponIds", required = false) java.util.List<Long> couponIds, 
            @RequestParam(value = "productIds", required = false) java.util.List<Long> productIds, 
            Model model) {
            
        if (result.hasErrors()) {
            // Gửi lại danh sách để hiện lên Modal nếu nhập lỗi
            model.addAttribute("coupons", couponRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            return slider.getId() == null ? "slider-create" : "slider-update"; 
        }
        
        // Bắn dữ liệu xuống Service để lưu
        sliderService.saveSlider(slider, couponIds, productIds);
        
        return "redirect:/sliders";
    }

    @PostMapping("/sliders/save")
    public String saveSlider(
            @jakarta.validation.Valid @ModelAttribute("slider") com.shoesshop.shoesshop.entity.Slider slider, 
            org.springframework.validation.BindingResult result, 
            @RequestParam(value = "couponIds", required = false) java.util.List<Long> couponIds, 
            @RequestParam(value = "productIds", required = false) java.util.List<Long> productIds, 
            org.springframework.ui.Model model) {
            
        if (result.hasErrors()) {
            model.addAttribute("coupons", couponRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            return slider.getId() == null ? "slider-create" : "slider-update"; 
        }
        
        sliderService.saveSlider(slider, couponIds, productIds);
        
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
        mockProducts.add(Map.of("id", 1, "name", "Nike Air Force 1", "price", 2500000));
        mockProducts.add(Map.of("id", 2, "name", "Adidas Ultraboost", "price", 3200000));
        model.addAttribute("products", mockProducts);

        return "slider-update";
    }

    @GetMapping("/sliders/delete/{id}")
    public String deleteSlider(@PathVariable Long id) {
        sliderService.deleteSlider(id);
        return "redirect:/sliders";
    }
}