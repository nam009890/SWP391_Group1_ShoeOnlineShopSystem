package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.service.SliderService;
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
            @Valid @ModelAttribute("slider") Slider slider,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> couponIds,
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Kiểm tra logic
        Map<String, String> logicErrors = sliderService.validateSliderLogic(slider, couponIds, productIds);
        if (!logicErrors.isEmpty()) {
            logicErrors.forEach((field, message) -> bindingResult.rejectValue(field, "error.slider", message));
        }

        // Validate file ảnh khi tạo mới
        if (slider.getId() == null && (imageFile == null || imageFile.isEmpty())) {
            bindingResult.rejectValue("imageUrl", "error.slider", "Please upload an image!");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("coupons", couponRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            return (slider.getId() == null) ? "slider-create" : "slider-update";
        }

        // Xử lý upload file
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                fileName = System.currentTimeMillis() + "_" + fileName; // Tránh trùng tên
                String uploadDir = "src/main/resources/static/uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    slider.setImageUrl("/uploads/" + fileName); // Đường dẫn lưu vào DB
                }
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload image!");
                return "redirect:/sliders";
            }
        }

        boolean isNew = (slider.getId() == null);
        sliderService.saveSlider(slider, couponIds, productIds);
        
        // Thêm thông báo
        redirectAttributes.addFlashAttribute("successMessage", 
                isNew ? "Slider created successfully!" : "Slider updated successfully!");
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