package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Category;
import Group1.ShoesOnlineShop.service.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    // ========================== LIST ==========================
    @GetMapping({"", "/"})
    public String listCategories(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {

        int size = 8;

        if ((keyword == null || keyword.trim().isEmpty()) && isActive == null) {
            // No filter -> return hierarchical list, paginate manually
            java.util.List<Category> allCategories = adminCategoryService.getHierarchicalCategoriesForList();
            
            int totalItems = allCategories.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            if (totalPages == 0) totalPages = 1;
            
            // Adjust page if it exceeds totalPages
            int currentPage = Math.min(page, totalPages);
            if (currentPage < 1) currentPage = 1;
            
            int start = (currentPage - 1) * size;
            int end = Math.min(start + size, totalItems);
            java.util.List<Category> paginatedList = allCategories.subList(start, end);

            model.addAttribute("categories", paginatedList);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", (long) totalItems);
            model.addAttribute("keyword", "");
            model.addAttribute("isActive", null);
        } else {
            Page<Category> categoryPage = adminCategoryService.getCategories(keyword, isActive, page, size);

            model.addAttribute("categories", categoryPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", categoryPage.getTotalPages());
            model.addAttribute("totalItems", categoryPage.getTotalElements());
            model.addAttribute("keyword", keyword);
            model.addAttribute("isActive", isActive);
        }

        return "admin-category-list";
    }

    // ========================== CREATE FORM ==========================
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("rootCategories", adminCategoryService.getRootCategories(null));
        return "admin-category-create";
    }

    // ========================== CREATE SUBMIT ==========================
    @PostMapping("/create")
    public String createSubmit(
            @ModelAttribute Category category,
            @RequestParam(value = "parentId", required = false) Long parentId,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Trim fields
        if (category.getName() != null) category.setName(category.getName().trim());

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
            model.addAttribute("rootCategories", adminCategoryService.getRootCategories(null));
            return "admin-category-create";
        }


        // Save
        adminCategoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Category \"" + category.getName() + "\" created successfully!");
        return "redirect:/admin/categories";
    }

    // ========================== DETAIL ==========================
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        Category category = adminCategoryService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        return "admin-category-detail";
    }

    // ========================== UPDATE FORM ==========================
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable(name = "id") Long id, Model model) {
        Category category = adminCategoryService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        model.addAttribute("rootCategories", adminCategoryService.getRootCategories(category));
        return "admin-category-update";
    }

    // ========================== UPDATE SUBMIT ==========================
    @PostMapping("/update/{id}")
    public String updateSubmit(
            @PathVariable(name = "id") Long id,
            @ModelAttribute Category category,
            @RequestParam(value = "parentId", required = false) Long parentId,
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
            model.addAttribute("rootCategories", adminCategoryService.getRootCategories(category));
            return "admin-category-update";
        }


        adminCategoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Category \"" + category.getName() + "\" updated successfully!");
        return "redirect:/admin/categories";
    }

    // ========================== DELETE ==========================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        Category category = adminCategoryService.getCategoryById(id);
        if (category != null) {
            adminCategoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category \"" + category.getName() + "\" deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found!");
        }
        return "redirect:/admin/categories";
    }
}
