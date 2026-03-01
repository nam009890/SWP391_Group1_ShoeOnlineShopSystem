/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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

import java.util.List;

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
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return "coupon-create"; // Nếu nhập sai (vd: % giảm giá > 50), bắt nhập lại
        }
        
        couponService.saveCoupon(coupon);
        session.setAttribute("message", "Save successfully!");
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
}
