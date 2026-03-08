/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MarketingController {

    @GetMapping("/home")
    public String showMarketingHome(Model model) {
        // Sau này bạn có thể gọi Service ở đây để đếm số lượng Coupon, Slider thật từ Database
        // Ví dụ: model.addAttribute("totalCoupons", couponService.countAll());
        
        // Tạm thời mình truyền một số liệu giả lập (mock data) để hiển thị lên giao diện
        model.addAttribute("activeSliders", 5);
        model.addAttribute("runningCoupons", 12);
        model.addAttribute("totalContents", 24);
        
        return "marketing-home"; // Trả về file HTML marketing-home.html
    }
}
