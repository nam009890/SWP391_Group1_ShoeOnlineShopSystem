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
public class CouponController {

    // CHỈ GỌI SERVICE - KIẾN TRÚC CHUẨN ĐI LÀM
    @Autowired
    private CouponService couponService;

    // 1. Hiển thị trang danh sách
   // 1. Hiển thị danh sách + Xử lý Filter
    @GetMapping("/coupons")
    public String listCoupons(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer discount,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) String validity,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        // GỌI SERVICE VỚI ĐỦ 6 THAM SỐ
        Page<Coupon> pageCoupons = couponService.getCoupons(keyword, discount, status, validity, page, size);

        model.addAttribute("coupons", pageCoupons.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageCoupons.getTotalPages());
        model.addAttribute("totalItems", pageCoupons.getTotalElements());
        
        // Lưu lại bộ lọc để giữ trạng thái cho giao diện
        model.addAttribute("keyword", keyword);
        model.addAttribute("discount", discount);
        model.addAttribute("status", status);
        model.addAttribute("validity", validity);
        
        // Truyền ngày hôm nay xuống để UI tự check Hết hạn hay Còn hạn
        model.addAttribute("today", java.time.LocalDate.now()); 

        return "coupon-list"; 
    }

    // 2. Hiển thị form tạo mới
    @GetMapping("/coupons/create")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "coupon-create"; 
    }

   @PostMapping("/coupons/save")
    public String saveCoupon(
            @Valid @ModelAttribute("coupon") Coupon coupon, 
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) { // THÊM Model model vào đây

        boolean isNew = (coupon.getId() == null);

        // Lỗi nhập liệu cơ bản (trống, sai định dạng)
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please check the highlighted fields and try again!");
            return isNew ? "coupon-create" : "coupon-update";
        }

        // Lỗi nghiệp vụ (Trùng tên, ngày sai)
        Map<String, String> logicErrors = couponService.validateCouponLogic(coupon);
        if (!logicErrors.isEmpty()) {
            logicErrors.forEach((field, message) -> bindingResult.rejectValue(field, "error.coupon", message));
            model.addAttribute("errorMessage", "Validation failed, please fix the errors below!");
            return isNew ? "coupon-create" : "coupon-update";
        }

        try {
            // Ép kiểu Status an toàn
            coupon.setIsActive(coupon.getIsActive() != null && coupon.getIsActive());
            couponService.saveCoupon(coupon);
            // Gắn thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", isNew ? "Coupon created successfully!" : "Coupon updated successfully!");
        } catch (Exception e) {
            // Gắn thông báo thất bại nếu hệ thống sập hoặc lỗi DB
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Failed to save coupon!");
        }
        
        return "redirect:/coupons";
    }

    // 4. Hiển thị form cập nhật
    @GetMapping("/coupons/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            return "redirect:/coupons";
        }
        model.addAttribute("coupon", coupon);
        return "coupon-update"; 
    }

    // 5. Xử lý xóa
    @GetMapping("/coupons/delete/{id}")
    public String deleteCoupon(@PathVariable Long id, HttpSession session) {
        couponService.deleteCoupon(id);
        session.setAttribute("message", "Delete successfully!");
        return "redirect:/coupons";
    }

    // 6. Hiển thị trang Detail
    @GetMapping("/coupons/detail/{id}")
    public String showCouponDetail(@PathVariable Long id, Model model) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            return "redirect:/coupons";
        }
        model.addAttribute("coupon", coupon);
        return "coupon-detail"; 
    }
}