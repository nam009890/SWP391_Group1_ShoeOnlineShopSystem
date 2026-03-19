package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.service.SliderService;
import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
            @RequestParam(required = false) Boolean status, // Filter Active/Deactive
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Slider> pageSliders = sliderService.getSliders(keyword, status, page, size);
        model.addAttribute("sliders", pageSliders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSliders.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status); 
        
        // THÊM DÒNG NÀY ĐỂ FIX LỖI THYMELEAF
        model.addAttribute("size", size); 
        
        return "slider-list";
    }

    @GetMapping("/sliders/create")
    public String showCreateSliderForm(Model model) {
        model.addAttribute("slider", new Slider());
        model.addAttribute("coupons", couponRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "slider-create";
    }

    @PostMapping("/sliders/save")
    public String saveSlider(
            @Valid @ModelAttribute("slider") Slider sliderForm,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> couponIds,
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam(required = false) List<Integer> productDiscounts,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean isUpdate = (sliderForm.getId() != null);

        // 1. Gọi Service để kiểm tra toàn bộ (Logic & Ảnh)
        Map<String, String> errors = sliderService.validateSlider(sliderForm, couponIds, productIds, imageFile);
        if (!errors.isEmpty()) {
            errors.forEach((field, message) -> bindingResult.rejectValue(field, "error.slider", message));
        }

        // 2. Nếu có lỗi -> Trả về giao diện ngay lập tức
        if (bindingResult.hasErrors()) {
            model.addAttribute("coupons", couponRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            
            // --- THÊM 6 DÒNG NÀY ĐỂ PHỤC HỒI SẢN PHẨM/COUPON KHI BỊ LỖI ---
            if (productIds != null && !productIds.isEmpty()) {
                List<Group1.ShoesOnlineShop.entity.Product> pdList = productRepository.findAllById(productIds);
                for (int i = 0; i < productIds.size(); i++) {
                    Long pId = productIds.get(i);
                    Integer discount = (productDiscounts != null && i < productDiscounts.size()) ? productDiscounts.get(i) : 0;
                    Product p = pdList.stream().filter(prod -> prod.getProductId().equals(pId)).findFirst().orElse(null);
                    if (p != null) {
                        sliderForm.addProduct(p, discount);
                    }
                }
            }
            if (couponIds != null && !couponIds.isEmpty()) {
                sliderForm.setCoupons(couponRepository.findAllById(couponIds));
            }
            // -------------------------------------------------------------

            // Nếu là form Update, lấy lại đường dẫn ảnh cũ để hiển thị preview
            if (isUpdate) {
                Slider existing = sliderService.getSliderById(sliderForm.getId());
                if (existing != null) sliderForm.setImageUrl(existing.getImageUrl());
            }
            return isUpdate ? "slider-update" : "slider-create";
        }

        // 3. Gọi Service xử lý lưu File và DB
        try {
            sliderService.processAndSaveSlider(sliderForm, couponIds, productIds, productDiscounts, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", 
                isUpdate ? "Slider updated successfully!" : "Slider created successfully!");
        } catch (Exception e) {
            // Đề phòng lỗi I/O khi lưu file ảnh
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving slider: " + e.getMessage());
        }

        return "redirect:/sliders";
    }

    @GetMapping("/sliders/update/{id}")
    public String showUpdateSliderForm(@PathVariable Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        if (slider == null) return "redirect:/sliders";
        model.addAttribute("slider", slider);
        model.addAttribute("coupons", couponRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "slider-update";
    }

    // [Tạo mới] Màn hình xem chi tiết
    @GetMapping("/sliders/detail/{id}")
    public String showSliderDetail(@PathVariable Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        if (slider == null) return "redirect:/sliders";
        model.addAttribute("slider", slider);
        return "slider-detail";
    }

    @GetMapping("/sliders/delete/{id}")
    public String deleteSlider(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        sliderService.deleteSlider(id);
        redirectAttributes.addFlashAttribute("successMessage", "Slider deleted successfully!");
        return "redirect:/sliders";
    }
}