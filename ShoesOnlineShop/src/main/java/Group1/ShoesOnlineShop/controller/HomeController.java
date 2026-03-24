package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Category;
import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.entity.Feedback;
import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.repository.CategoryRepository;
import Group1.ShoesOnlineShop.repository.ContentRepository;
import Group1.ShoesOnlineShop.repository.FeedbackRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.repository.SliderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private Map<Long, Integer> getActiveProductDiscounts() {
        List<Slider> activeSliders = sliderRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        Map<Long, Integer> productDiscounts = new HashMap<>();
        for (Slider s : activeSliders) {
            for (Group1.ShoesOnlineShop.entity.SliderProduct sp : s.getSliderProducts()) {
                Long pid = sp.getProduct().getId();
                int currentDis = productDiscounts.getOrDefault(pid, 0);
                if (sp.getDiscount() > currentDis) {
                    productDiscounts.put(pid, sp.getDiscount());
                }
            }
        }
        return productDiscounts;
    }

    // ===================== HOME PAGE =====================
    @GetMapping({"/", "/home"})
    public String showHomePage(Model model) {
        List<Category> rootCategories = categoryRepository
                .findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
        List<Slider> sliders = sliderRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        List<Product> featuredProducts = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        if (featuredProducts.size() > 12) {
            featuredProducts = featuredProducts.subList(0, 12);
        }
        List<Content> contents = contentRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        if (contents.size() > 6) {
            contents = contents.subList(0, 6);
        }

        model.addAttribute("rootCategories", rootCategories);
        model.addAttribute("sliders", sliders);
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("contents", contents);
        model.addAttribute("productDiscounts", getActiveProductDiscounts());
        return "home";
    }

    // ===================== AJAX FILTER =====================
    @GetMapping("/api/home/products")
    @ResponseBody
    public List<Map<String, Object>> filterProducts(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {

        List<Product> products;
        List<Long> categoryIds = null;
        if (categoryId != null) {
            categoryIds = new ArrayList<>();
            categoryIds.add(categoryId);
            Category cat = categoryRepository.findById(categoryId).orElse(null);
            if (cat != null && cat.getChildren() != null) {
                for (Category child : cat.getChildren()) {
                    if (Boolean.TRUE.equals(child.getIsActive())) {
                        categoryIds.add(child.getId());
                    }
                }
            }
        }

        boolean hasCategory = categoryIds != null && !categoryIds.isEmpty();
        boolean hasPrice = minPrice != null && maxPrice != null;

        if (hasCategory && hasPrice) {
            products = productRepository.findByIsActiveTrueAndCategory_IdInAndPriceBetweenOrderByCreatedAtDesc(categoryIds, minPrice, maxPrice);
        } else if (hasCategory) {
            products = productRepository.findByIsActiveTrueAndCategory_IdInOrderByCreatedAtDesc(categoryIds);
        } else if (hasPrice) {
            products = productRepository.findByIsActiveTrueAndPriceBetweenOrderByCreatedAtDesc(minPrice, maxPrice);
        } else {
            products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        }

        if (products.size() > 12) {
            products = products.subList(0, 12);
        }

        Map<Long, Integer> discounts = getActiveProductDiscounts();

        return products.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            
            int discount = discounts.getOrDefault(p.getId(), 0);
            if (discount > 0) {
                BigDecimal finalPrice = p.getPrice().subtract(p.getPrice().multiply(new BigDecimal(discount)).divide(new BigDecimal(100)));
                map.put("originalPrice", p.getPrice());
                map.put("discount", discount);
                map.put("price", finalPrice);
            } else {
                map.put("price", p.getPrice());
            }

            map.put("imageUrl", p.getImageUrl());
            map.put("categoryName", p.getCategory() != null ? p.getCategory().getName() : "");
            return map;
        }).collect(Collectors.toList());
    }

    // ===================== SLIDER DETAIL =====================
    @GetMapping("/slider/{id}")
    public String showSliderDetail(@PathVariable("id") Long id, Model model) {
        Slider slider = sliderRepository.findById(id).orElse(null);
        if (slider == null) {
            return "redirect:/home";
        }
        model.addAttribute("slider", slider);
        return "customer-slider-detail";
    }

    // ===================== PRODUCT LIST =====================
    @GetMapping("/products")
    public String showProductList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Category> rootCategories = categoryRepository
                .findByParentIsNullAndIsActiveTrueOrderByDisplayOrderAscNameAsc();

        List<Product> products;
        List<Long> categoryIds = null;
        if (categoryId != null) {
            categoryIds = new ArrayList<>();
            categoryIds.add(categoryId);
            Category cat = categoryRepository.findById(categoryId).orElse(null);
            if (cat != null && cat.getChildren() != null) {
                for (Category child : cat.getChildren()) {
                    if (Boolean.TRUE.equals(child.getIsActive())) {
                        categoryIds.add(child.getId());
                    }
                }
            }
        }

        boolean hasCategory = categoryIds != null && !categoryIds.isEmpty();
        boolean hasPrice = minPrice != null && maxPrice != null;

        if (hasCategory && hasPrice) {
            products = productRepository.findByIsActiveTrueAndCategory_IdInAndPriceBetweenOrderByCreatedAtDesc(categoryIds, minPrice, maxPrice);
        } else if (hasCategory) {
            products = productRepository.findByIsActiveTrueAndCategory_IdInOrderByCreatedAtDesc(categoryIds);
        } else if (hasPrice) {
            products = productRepository.findByIsActiveTrueAndPriceBetweenOrderByCreatedAtDesc(minPrice, maxPrice);
        } else {
            products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        }

        model.addAttribute("products", products);
        model.addAttribute("rootCategories", rootCategories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);
        model.addAttribute("keyword", keyword);
        model.addAttribute("productDiscounts", getActiveProductDiscounts());
        return "customer-product-list";
    }

    // ===================== PRODUCT DETAIL =====================
    @GetMapping("/product/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }

        // Approved feedbacks for this product
        List<Feedback> feedbacks = feedbackRepository
                .findByProduct_IdAndIsApprovedTrueOrderByCreatedAtDesc(id);

        // Related products (same category, exclude current)
        List<Product> relatedProducts = new ArrayList<>();
        if (product.getCategory() != null) {
            List<Long> catIds = new ArrayList<>();
            catIds.add(product.getCategory().getId());
            relatedProducts = productRepository
                    .findByIsActiveTrueAndCategory_IdInOrderByCreatedAtDesc(catIds);
            relatedProducts.removeIf(p -> p.getId().equals(id));
            if (relatedProducts.size() > 4) {
                relatedProducts = relatedProducts.subList(0, 4);
            }
        }

        // Calculate average rating
        double avgRating = 0;
        if (!feedbacks.isEmpty()) {
            avgRating = feedbacks.stream()
                    .filter(f -> f.getRating() != null)
                    .mapToInt(Feedback::getRating)
                    .average()
                    .orElse(0);
        }

        model.addAttribute("product", product);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("feedbackCount", feedbacks.size());
        model.addAttribute("productDiscounts", getActiveProductDiscounts());
        return "customer-product-detail";
    }

    // ===================== CONTENT DETAIL =====================
    @GetMapping("/content/{id}")
    public String showContentDetail(@PathVariable("id") Long id, Model model) {
        Content content = contentRepository.findById(id).orElse(null);
        if (content == null) {
            return "redirect:/home";
        }
        model.addAttribute("content", content);
        return "customer-content-detail";
    }
}
