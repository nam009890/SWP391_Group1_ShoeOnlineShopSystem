package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.service.CouponService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@Controller
@RequestMapping("/internal/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

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

        return "coupon-list"; 
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "coupon-create"; 
    }

    @PostMapping("/save")
    public String saveCoupon(
            @Valid @ModelAttribute("coupon") Coupon coupon, 
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean isNew = (coupon.getId() == null);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please check the highlighted fields and try again!");
            return isNew ? "coupon-create" : "coupon-update";
        }

        Map<String, String> logicErrors = couponService.validateCouponLogic(coupon);
        if (!logicErrors.isEmpty()) {
            logicErrors.forEach((field, message) -> bindingResult.rejectValue(field, "error.coupon", message));
            model.addAttribute("errorMessage", "Validation failed, please fix the errors below!");
            return isNew ? "coupon-create" : "coupon-update";
        }

        try {
            coupon.setIsActive(coupon.getIsActive() != null && coupon.getIsActive());
            couponService.saveCoupon(coupon);
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
        return "coupon-update"; 
    }

    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable(name = "id") Long id, HttpSession session) {
        couponService.deleteCoupon(id);
        session.setAttribute("message", "Delete successfully!");
        return "redirect:/internal/coupons";
    }

    @GetMapping("/detail/{id}")
    public String showCouponDetail(@PathVariable(name = "id") Long id, Model model) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            return "redirect:/internal/coupons";
        }
        model.addAttribute("coupon", coupon);
        return "coupon-detail"; 
    }
}