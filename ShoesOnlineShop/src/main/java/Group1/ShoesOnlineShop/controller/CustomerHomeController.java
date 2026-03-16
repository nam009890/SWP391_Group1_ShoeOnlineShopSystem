package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.service.ContentService;
import Group1.ShoesOnlineShop.service.CustomerProductService;
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

    @GetMapping({"/", "/customer-home"})
    public String home(Model model) {
        // Get active sliders
        Page<Slider> sliders = sliderService.getSliders("", true, 1, 5);
        model.addAttribute("sliders", sliders.getContent());
        
        // Get banners (Contents)
        model.addAttribute("banners", contentService.getContents("", "Banner", 1, 3).getContent());
        
        // Get featured products
        model.addAttribute("featuredProducts", customerProductService.getFeaturedProducts());
        
        return "customer-home";
    }
}
