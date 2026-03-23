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
@RequestMapping("/internal/sliders")
public class SliderController {

    @Autowired
    private SliderService sliderService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public String listSliders(
            Model model,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        Page<Slider> pageSliders = sliderService.getSliders(keyword, status, page, size);
        model.addAttribute("sliders", pageSliders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSliders.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status); 
        model.addAttribute("size", size); 
        return "slider-list";
    }

    @GetMapping("/create")
    public String showCreateSliderForm(Model model) {
        model.addAttribute("slider", new Slider());
        model.addAttribute("coupons", couponRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "slider-create";
    }

    @PostMapping("/save")
    public String saveSlider(
            @Valid @ModelAttribute("slider") Slider sliderForm,
            BindingResult bindingResult,
            @RequestParam(name = "couponIds", required = false) List<Long> couponIds,
            @RequestParam(name = "productIds", required = false) List<Long> productIds,
            @RequestParam(name = "productDiscounts", required = false) List<Integer> productDiscounts,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean isUpdate = (sliderForm.getId() != null);

        Map<String, String> errors = sliderService.validateSlider(sliderForm, couponIds, productIds, imageFile);
        if (!errors.isEmpty()) {
            errors.forEach((field, message) -> bindingResult.rejectValue(field, "error.slider", message));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("coupons", couponRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            
            if (productIds != null && !productIds.isEmpty()) {
                List<Product> pdList = productRepository.findAllById(productIds);
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

            if (isUpdate) {
                Slider existing = sliderService.getSliderById(sliderForm.getId());
                if (existing != null) sliderForm.setImageUrl(existing.getImageUrl());
            }
            return isUpdate ? "slider-update" : "slider-create";
        }

        try {
            sliderService.processAndSaveSlider(sliderForm, couponIds, productIds, productDiscounts, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", 
                isUpdate ? "Slider updated successfully!" : "Slider created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving slider: " + e.getMessage());
        }

        return "redirect:/internal/sliders";
    }

    @GetMapping("/update/{id}")
    public String showUpdateSliderForm(@PathVariable(name = "id") Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        if (slider == null) return "redirect:/internal/sliders";
        model.addAttribute("slider", slider);
        model.addAttribute("coupons", couponRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        return "slider-update";
    }

    @GetMapping("/detail/{id}")
    public String showSliderDetail(@PathVariable(name = "id") Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        if (slider == null) return "redirect:/internal/sliders";
        model.addAttribute("slider", slider);
        return "slider-detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteSlider(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        sliderService.deleteSlider(id);
        redirectAttributes.addFlashAttribute("successMessage", "Slider deleted successfully!");
        return "redirect:/internal/sliders";
    }
}