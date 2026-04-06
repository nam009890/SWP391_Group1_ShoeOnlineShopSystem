package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.service.CouponService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/internal/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public String listCoupons(
            Model model,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "discount", required = false) Integer discount,
            @RequestParam(name = "status", required = false) Boolean status,
            @RequestParam(name = "validity", required = false) String validity,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        Page<Coupon> pageCoupons = couponService.getCoupons(keyword, discount, status, validity, page, size);

        model.addAttribute("coupons", pageCoupons.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageCoupons.getTotalPages());
        model.addAttribute("totalItems", pageCoupons.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("discount", discount);
        model.addAttribute("status", status);
        model.addAttribute("validity", validity);
        model.addAttribute("today", java.time.LocalDate.now()); 

        return "marketing/coupon-list"; 
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        model.addAttribute("allProducts", productRepository.findAll());
        return "marketing/coupon-create"; 
    }

    @PostMapping("/save")
    public String saveCoupon(
            @Valid @ModelAttribute("coupon") Coupon couponForm, 
            BindingResult bindingResult,
            @RequestParam(name = "products", required = false) List<Long> productIds,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean isNew = (couponForm.getId() == null);

        List<Group1.ShoesOnlineShop.entity.Product> selectedProducts = new ArrayList<>();
        if (productIds != null && !productIds.isEmpty()) {
            selectedProducts = productRepository.findAllById(productIds);
        }
        couponForm.setProducts(selectedProducts);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please check the highlighted fields and try again!");

            model.addAttribute("allProducts", productRepository.findAll());

            return isNew ? "marketing/coupon-create" : "marketing/coupon-update";
        }

        Map<String, String> logicErrors = couponService.validateCouponLogic(couponForm);
        if (!logicErrors.isEmpty()) {
            logicErrors.forEach((field, message) -> bindingResult.rejectValue(field, "error.coupon", message));
            model.addAttribute("errorMessage", "Validation failed, please fix the errors below!");

            model.addAttribute("allProducts", productRepository.findAll());

            return isNew ? "marketing/coupon-create" : "marketing/coupon-update";
        }

        try {
            Coupon targetCoupon;
            if (!isNew) {
                targetCoupon = couponService.getCouponById(couponForm.getId());
                if (targetCoupon == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Coupon not found!");
                    return "redirect:/internal/coupons";
                }
            } else {
                targetCoupon = new Coupon();
                targetCoupon.setCreateDate(java.time.LocalDate.now());
            }

            // Map form fields to target entity to prevent losing unmapped DB fields
            targetCoupon.setCouponName(couponForm.getCouponName());
            targetCoupon.setCouponCode(couponForm.getCouponCode());
            targetCoupon.setDiscountType(couponForm.getDiscountType());
            targetCoupon.setDiscountValue(couponForm.getDiscountValue());
            targetCoupon.setMaxDiscountAmount(couponForm.getMaxDiscountAmount());
            targetCoupon.setMinOrderValue(couponForm.getMinOrderValue());
            if (couponForm.getCreateDate() != null) targetCoupon.setCreateDate(couponForm.getCreateDate());
            targetCoupon.setEndDate(couponForm.getEndDate());
            targetCoupon.setScope(couponForm.getScope());
            targetCoupon.setUpdateNote(couponForm.getUpdateNote());
            targetCoupon.setIsActive(couponForm.getIsActive() != null && couponForm.getIsActive());
            targetCoupon.setQuantity(couponForm.getQuantity());

            if ("SPECIFIC_PRODUCTS".equals(couponForm.getScope())) {
                targetCoupon.setProducts(selectedProducts);
            } else {
                targetCoupon.setProducts(new ArrayList<>());
            }

            couponService.saveCoupon(targetCoupon);
            redirectAttributes.addFlashAttribute("successMessage", isNew ? "Coupon created successfully!" : "Coupon updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Failed to save coupon!");
        }
        
        return "redirect:/internal/coupons";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable(name = "id") Long id, Model model) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            return "redirect:/internal/coupons";
        }
        model.addAttribute("coupon", coupon);
        model.addAttribute("allProducts", productRepository.findAll());
        return "marketing/coupon-update"; 
    }

    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable(name = "id") Long id, HttpSession session) {
        couponService.requestDelete(id);
        session.setAttribute("message", "Delete request sent to Admin!");
        return "redirect:/internal/coupons";
    }

    @GetMapping("/detail/{id}")
    public String showCouponDetail(@PathVariable(name = "id") Long id, Model model) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            return "redirect:/internal/coupons";
        }
        model.addAttribute("coupon", coupon);

        model.addAttribute("today", java.time.LocalDate.now());
        return "marketing/coupon-detail"; 
    }
}


