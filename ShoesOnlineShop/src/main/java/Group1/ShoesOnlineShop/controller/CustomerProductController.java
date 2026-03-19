package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.service.CustomerProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import Group1.ShoesOnlineShop.service.FeedbackService;

@Controller
public class CustomerProductController {

    @Autowired
    private CustomerProductService customerProductService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/products")
    public String listProducts(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<Product> productPage = customerProductService.getActiveProducts(keyword, category, page, size);
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        
        // Keep filter state
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        
        return "customer-product-list";
    }

    @GetMapping("/products/detail/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = customerProductService.getProductById(id);
        if (product == null) {
            return "redirect:/products";
        }
        
        model.addAttribute("product", product);
        
        // Get feedbacks for this product and add to model
        model.addAttribute("feedbacks", feedbackService.getFeedbacksByProduct(id));
        
        return "customer-product-detail";
    }
}
