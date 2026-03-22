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
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "12") int size
    ) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            Page<Product> productPage = customerProductService.getActiveProducts(keyword, category, sort, page, size);
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalItems", productPage.getTotalElements());
        } else if ("newest".equalsIgnoreCase(sort)) {
            java.util.List<Product> products = customerProductService.getNewest();
            model.addAttribute("products", products);
            model.addAttribute("totalItems", (long) products.size());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
        } else if (brand != null && !brand.trim().isEmpty()) {
            java.util.List<Product> products = customerProductService.getByBrand(brand);
            model.addAttribute("products", products);
            model.addAttribute("totalItems", (long) products.size());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
        } else if (category != null && !category.trim().isEmpty()) {
            java.util.List<Product> products = customerProductService.getByCategory(category);
            model.addAttribute("products", products);
            model.addAttribute("totalItems", (long) products.size());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
        } else {
            java.util.List<Product> products = customerProductService.getAll();
            model.addAttribute("products", products);
            model.addAttribute("totalItems", (long) products.size());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", 1);
        }
        
        // Keep filter state
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("brand", brand);
        model.addAttribute("sort", sort);
        
        return "customer-products";
    }

    @GetMapping("/products/detail/{id}")
    public String productDetail(@PathVariable(name = "id") Long id, Model model) {
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
