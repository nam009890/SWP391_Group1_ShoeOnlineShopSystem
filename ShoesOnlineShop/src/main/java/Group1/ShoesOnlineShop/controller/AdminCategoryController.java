package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Category;
import Group1.ShoesOnlineShop.service.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    // ========================== LIST ==========================
    @GetMapping({"", "/"})
    public String listCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        int size = 8;
        Page<Category> categoryPage = adminCategoryService.getCategories(keyword, isActive, page, size);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("isActive", isActive);

        return "admin-category-list";
    }

    // ========================== CREATE FORM ==========================
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(null));
        return "admin-category-create";
    }

    // ========================== CREATE SUBMIT ==========================
    @PostMapping("/create")
    public String createSubmit(
            @ModelAttribute Category category,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Trim fields
        if (category.getName() != null) category.setName(category.getName().trim());
        if (category.getDescription() != null) category.setDescription(category.getDescription().trim());

        // Assign parent
        if (parentId != null) {
            category.setParent(adminCategoryService.getCategoryById(parentId));
        } else {
            category.setParent(null);
        }

        // Validate
        Map<String, String> errors = adminCategoryService.validateCategory(category);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("category", category);
            model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(null));
            return "admin-category-create";
        }

        // Upload image
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = adminCategoryService.uploadImage(imageFile);
                category.setImageUrl(imageUrl);
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errors", Map.of("imageFile", e.getMessage()));
            model.addAttribute("category", category);
            model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(null));
            return "admin-category-create";
        } catch (IOException e) {
            model.addAttribute("errors", Map.of("imageFile", "Lỗi khi upload ảnh! Thử lại."));
            model.addAttribute("category", category);
            model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(null));
            return "admin-category-create";
        }

        // Save
        adminCategoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo danh mục \"" + category.getName() + "\" thành công!");
        return "redirect:/admin/categories";
    }

    // ========================== DETAIL ==========================
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Category category = adminCategoryService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        return "admin-category-detail";
    }

    // ========================== UPDATE FORM ==========================
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Category category = adminCategoryService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(category));
        return "admin-category-update";
    }

    // ========================== UPDATE SUBMIT ==========================
    @PostMapping("/update/{id}")
    public String updateSubmit(
            @PathVariable Long id,
            @ModelAttribute Category category,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "keepOldImage", required = false) String keepOldImage,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Fetch existing category
        Category existing = adminCategoryService.getCategoryById(id);
        if (existing == null) {
            return "redirect:/admin/categories";
        }

        // Preserve id
        category.setId(id);
        category.setCreatedAt(existing.getCreatedAt());

        // Trim
        if (category.getName() != null) category.setName(category.getName().trim());
        if (category.getDescription() != null) category.setDescription(category.getDescription().trim());

        // Assign parent
        if (parentId != null) {
            category.setParent(adminCategoryService.getCategoryById(parentId));
        } else {
            category.setParent(null);
        }

        // Validate
        Map<String, String> errors = adminCategoryService.validateCategory(category);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("category", category);
            model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(category));
            return "admin-category-update";
        }

        // Image logic: upload new OR keep old
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = adminCategoryService.uploadImage(imageFile);
                category.setImageUrl(imageUrl);
            } else {
                category.setImageUrl(existing.getImageUrl()); // giữ ảnh cũ
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errors", Map.of("imageFile", e.getMessage()));
            model.addAttribute("category", category);
            model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(category));
            return "admin-category-update";
        } catch (IOException e) {
            model.addAttribute("errors", Map.of("imageFile", "Lỗi khi upload ảnh! Thử lại."));
            model.addAttribute("category", category);
            model.addAttribute("allCategories", adminCategoryService.getFlattenedCategories(category));
            return "admin-category-update";
        }

        adminCategoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục \"" + category.getName() + "\" thành công!");
        return "redirect:/admin/categories";
    }

    // ========================== DELETE ==========================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Category category = adminCategoryService.getCategoryById(id);
        if (category != null) {
            adminCategoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa danh mục \"" + category.getName() + "\"!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục!");
        }
        return "redirect:/admin/categories";
    }
}
