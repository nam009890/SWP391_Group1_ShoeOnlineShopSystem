package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.service.ContentService;
import Group1.ShoesOnlineShop.service.CustomerProductService;
import Group1.ShoesOnlineShop.service.CouponService;
import Group1.ShoesOnlineShop.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerHomeController {

    @Autowired
    private SliderService sliderService;
    
    @Autowired
    private ContentService contentService;
    
    @Autowired
    private CustomerProductService customerProductService;

    @Autowired
    private CouponService couponService;

    @GetMapping({"/", "/customer-home"})
    public String home(Model model) {
        // Get active sliders
        java.util.List<Slider> sliders = sliderService.getActiveSliders();
        model.addAttribute("sliders", sliders);
        
        // Get banners (Contents) - Only active ones
        model.addAttribute("banners", contentService.getContents("", "Banner", true, 1, 3).getContent());
        
        // Get featured products
        model.addAttribute("featuredProducts", customerProductService.getFeaturedProducts());
        
        // Get available coupons
        model.addAttribute("availableCoupons", couponService.getActiveCoupons());
        
        return "customer-home";
    }
}
