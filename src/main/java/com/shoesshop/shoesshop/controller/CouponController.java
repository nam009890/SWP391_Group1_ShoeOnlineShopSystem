package com.shoesshop.shoesshop.controller;

import com.shoesshop.shoesshop.entity.Coupon;
import com.shoesshop.shoesshop.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.List;
import java.util.ArrayList;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;


@Controller 
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;

    @GetMapping("/coupons")
    public String listCoupons(
            Model model,
            @RequestParam(defaultValue = "") String keyword, 
            @RequestParam(defaultValue = "1") int page,      
            @RequestParam(defaultValue = "5") int size       
    ) {
        Pageable paging = PageRequest.of(page - 1, size);
        
        Page<Coupon> pageCoupons;

        if (keyword == null || keyword.isEmpty()) {
            pageCoupons = couponRepository.findAll(paging);
        } else {
            pageCoupons = couponRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword, paging);
        }

        model.addAttribute("coupons", pageCoupons.getContent()); 
        model.addAttribute("currentPage", page); 
        model.addAttribute("totalPages", pageCoupons.getTotalPages()); 
        model.addAttribute("totalItems", pageCoupons.getTotalElements()); 
        model.addAttribute("keyword", keyword); 

        return "coupon-list"; 
    }
    // 1. Hàm hiện form tạo mới
// 1. Hàm hiện Form (GET)
    @GetMapping("/coupons/create")
    public String showCreateForm(Model model, HttpSession session, 
                                 @RequestParam(value = "reset", required = false) boolean reset) {
        
        // Nếu người dùng bấm từ Menu (có tham số ?reset=true) -> Xóa sạch danh sách cũ
        if (reset) {
            session.removeAttribute("mySessionCoupons");
        }

        // Lấy danh sách từ Session ra
        List<Coupon> mySessionCoupons = (List<Coupon>) session.getAttribute("mySessionCoupons");
        if (mySessionCoupons == null) {
            mySessionCoupons = new ArrayList<>(); // Nếu chưa có thì tạo list rỗng
        }

        model.addAttribute("coupon", new Coupon());
        model.addAttribute("recentCoupons", mySessionCoupons); // Truyền list phiên làm việc sang View
        
        return "coupon-create";
    }

    // 2. Hàm xử lý lưu (Save)
@PostMapping("/coupons/save")
    public String saveCoupon(@Valid @ModelAttribute("coupon") Coupon coupon, 
                             BindingResult result, 
                             Model model,
                             HttpSession session) { // <--- BẮT BUỘC PHẢI CÓ DÒNG NÀY
        
        // Validate thủ công: Ngày kết thúc phải sau ngày bắt đầu
        if (coupon.getCreateDate() != null && coupon.getEndDate() != null) {
            if (coupon.getEndDate().isBefore(coupon.getCreateDate())) {
                result.rejectValue("endDate", "error.coupon", "End date must be after start date");
            }
        }

        // Nếu có lỗi (Validate sai) -> Trả về trang form để hiện lỗi
        if (result.hasErrors()) {
            // Load lại list bên dưới để không bị mất bảng
            List<Coupon> recentCoupons = couponRepository.findAll(PageRequest.of(0, 5)).getContent();
            model.addAttribute("recentCoupons", recentCoupons);
            return "coupon-create";
        }
if (result.hasErrors()) {
            // Nếu lỗi thì vẫn phải load lại list từ session để không bị mất bảng
            List<Coupon> mySessionCoupons = (List<Coupon>) session.getAttribute("mySessionCoupons");
            if (mySessionCoupons == null) mySessionCoupons = new ArrayList<>();
            model.addAttribute("recentCoupons", mySessionCoupons);
            return "coupon-create";
        }

        // 1. Lưu vào Database thật
        Coupon savedCoupon = couponRepository.save(coupon);

        // 2. Lưu vào Session (Để hiện xuống bảng danh sách chờ)
        List<Coupon> mySessionCoupons = (List<Coupon>) session.getAttribute("mySessionCoupons");
        if (mySessionCoupons == null) {
            mySessionCoupons = new ArrayList<>();
        }
        // Thêm vào ĐẦU danh sách (index 0) để cái mới nhất hiện lên trên cùng
        mySessionCoupons.add(0, savedCoupon);
        
        // Cập nhật lại session
        session.setAttribute("mySessionCoupons", mySessionCoupons);
        
        return "redirect:/coupons/create"; // Quay lại trang create (không có reset)
    }
   // 3. Hàm hiện form Sửa (Edit)
    @GetMapping("/coupons/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        // Tìm coupon theo ID
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon Id:" + id));
        
        model.addAttribute("coupon", coupon);
        
        // Không cần model.addAttribute("recentCoupons") vì màn hình Update không hiện bảng
        
        return "coupon-create"; // Vẫn dùng chung file giao diện với trang Create
    }
// 4. Hàm Xóa (Nâng cấp)
 // Trong CouponController.java

@GetMapping("/coupons/delete/{id}")
public String deleteCoupon(@PathVariable("id") Long id, 
                           @RequestParam(value = "redirect", required = false) String redirectTarget) {
    
    // 1. Xóa
    couponRepository.deleteById(id);
    
    // 2. Logic điều hướng
    if ("create".equals(redirectTarget)) {
        return "redirect:/coupons/create"; // <-- Dòng này giữ bạn ở lại trang Create
    }
    
    return "redirect:/coupons"; // Mặc định về trang danh sách to
}
}