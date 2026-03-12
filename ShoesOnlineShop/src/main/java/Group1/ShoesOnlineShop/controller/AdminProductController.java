package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.service.AdminProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private AdminProductService adminProductService;

    // 1. Danh sách sản phẩm
    @GetMapping
    public String listProducts(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<Product> pageProducts = adminProductService.getProducts(keyword, category, isActive, page, size);
        List<String> categories = adminProductService.getAllCategories();

        model.addAttribute("products", pageProducts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("isActive", isActive);
        model.addAttribute("categories", categories);

        return "admin-product-list";
    }

    // 2. Form tạo mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", adminProductService.getAllCategories());
        return "admin-product-create";
    }

    // 3. Xử lý lưu (Create & Update)
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model
    ) throws IOException {
        boolean isNew = (product.getId() == null);

        // Validate chung
        Map<String, String> errors = adminProductService.validateProduct(product);

        if (bindingResult.hasErrors() || !errors.isEmpty()) {
            errors.forEach((field, msg) -> bindingResult.rejectValue(field, "error.product", msg));
            model.addAttribute("categories", adminProductService.getAllCategories());
            return isNew ? "admin-product-create" : "admin-product-update";
        }

        // Upload ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = adminProductService.uploadImage(imageFile);
                product.setImageUrl(imageUrl);
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("imageUrl", "error.product", e.getMessage());
                model.addAttribute("categories", adminProductService.getAllCategories());
                return isNew ? "admin-product-create" : "admin-product-update";
            }
        }

        try {
            product.setIsActive(product.getIsActive() != null && product.getIsActive());
            adminProductService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage",
                    isNew ? "Product created successfully!" : "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Failed to save product!");
        }

        return "redirect:/admin/products";
    }

    // 4. Form cập nhật
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Product product = adminProductService.getProductById(id);
        if (product == null) return "redirect:/admin/products";

        model.addAttribute("product", product);
        model.addAttribute("categories", adminProductService.getAllCategories());
        return "admin-product-update";
    }

    // 5. Chi tiết
    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        Product product = adminProductService.getProductById(id);
        if (product == null) return "redirect:/admin/products";

        model.addAttribute("product", product);
        return "admin-product-detail";
    }

    // 6. Xóa
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminProductService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete product! It may be associated with existing orders.");
        }
        return "redirect:/admin/products";
    }
}
