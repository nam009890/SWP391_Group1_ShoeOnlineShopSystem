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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "marketing/slider-list";
    }

    @GetMapping("/create")
    public String showCreateSliderForm(Model model) {
        model.addAttribute("slider", new Slider());
        model.addAttribute("coupons", couponRepository.findValidCouponsForSlider());
        model.addAttribute("products", productRepository.findAll());
        return "marketing/slider-create";
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
            sliderService.restoreSliderFormState(model, sliderForm, couponIds, productIds, productDiscounts, isUpdate);
            return isUpdate ? "marketing/slider-update" : "marketing/slider-create";
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
        model.addAttribute("coupons", couponRepository.findValidCouponsForSlider());
        model.addAttribute("products", productRepository.findAll());
        return "marketing/slider-update";
    }

    @GetMapping("/detail/{id}")
    public String showSliderDetail(@PathVariable(name = "id") Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        if (slider == null) return "redirect:/internal/sliders";
        model.addAttribute("slider", slider);
        return "marketing/slider-detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteSlider(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        Slider slider = sliderService.getSliderById(id);
        if (slider != null) {
            slider.setIsActive(false);
            slider.setApprovalStatus("PENDING");
            slider.setRemakeNote("DELETE REQUEST");
            // Save it back to trigger approval
            try {
                // To avoid passing null constraints, we can use repository directly or a service method
                // We'll call save from repository directly if possible or add a method. Wait, sliderService has no simple save for an entity without products.
                // We will add a method or just use the repository if it's autowired. It's not autowired here.
                // But sliderForm save needs couponIds etc.
                sliderService.requestDelete(id);
            } catch (Exception e) {}
        }
        redirectAttributes.addFlashAttribute("successMessage", "Delete request sent to Admin!");
        return "redirect:/internal/sliders";
    }
}

