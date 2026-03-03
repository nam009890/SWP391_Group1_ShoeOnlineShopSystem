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

import java.util.Map;

@Controller
public class CouponController {

    // CHỈ GỌI SERVICE - KIẾN TRÚC CHUẨN ĐI LÀM
    @Autowired
    private CouponService couponService;

    // 1. Hiển thị trang danh sách
    @GetMapping("/coupons")
    public String listCoupons(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Coupon> pageCoupons = couponService.getCoupons(keyword, page, size);

        model.addAttribute("coupons", pageCoupons.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageCoupons.getTotalPages());
        model.addAttribute("totalItems", pageCoupons.getTotalElements());
        model.addAttribute("keyword", keyword);

        return "coupon-list"; 
    }

    // 2. Hiển thị form tạo mới
    @GetMapping("/coupons/create")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "coupon-create"; 
    }

    // 3. Xử lý khi ấn nút Submit lưu dữ liệu
    @PostMapping("/coupons/save")
    public String saveCoupon(
            @Valid @ModelAttribute("coupon") Coupon coupon, 
            BindingResult bindingResult) {

        // Bước 1: Entity có bị trống, sai định dạng (Lỗi cơ bản) không?
        if (bindingResult.hasErrors()) {
            return (coupon.getId() == null) ? "coupon-create" : "coupon-update";
        }

        // Bước 2: Gọi Service để kiểm tra lỗi nghiệp vụ (Trùng lặp, so sánh ngày)
        Map<String, String> logicErrors = couponService.validateCouponLogic(coupon);
        
        // Nếu Service phát hiện có lỗi -> Nhét lỗi vào BindingResult để view HTML in chữ màu đỏ ra
        if (!logicErrors.isEmpty()) {
            logicErrors.forEach((field, message) -> 
                bindingResult.rejectValue(field, "error.coupon", message)
            );
            return (coupon.getId() == null) ? "coupon-create" : "coupon-update";
        }

        // Bước 3: Vượt qua mọi rào cản -> Lưu thành công!
        couponService.saveCoupon(coupon);
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