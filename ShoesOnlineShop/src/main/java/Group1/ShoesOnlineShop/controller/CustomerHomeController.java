package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.service.ContentService;
import Group1.ShoesOnlineShop.service.CustomerProductService;
import Group1.ShoesOnlineShop.service.CouponService;
import Group1.ShoesOnlineShop.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        java.util.List<Slider> sliders = sliderService.getActiveSliders();
        model.addAttribute("sliders", sliders);
        model.addAttribute("banners", contentService.getContents("", "Banner", true, 1, 3).getContent());
        model.addAttribute("featuredProducts", customerProductService.getFeaturedProducts());
        model.addAttribute("availableCoupons", couponService.getActiveCoupons());
        return "customer-home";
    }

    @GetMapping("/sliders/{id}")
    public String sliderDetail(@PathVariable(name = "id") Long id, Model model) {
        Slider slider = sliderService.getSliderById(id);
        if (slider == null) {
            return "redirect:/";
        }
        model.addAttribute("slider", slider);
        return "customer-slider-detail";
    }
}
