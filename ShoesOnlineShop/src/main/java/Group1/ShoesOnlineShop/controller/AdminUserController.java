package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // 1. Danh sách users
    @GetMapping
    public String listUsers(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<User> pageUsers = adminUserService.getUsers(keyword, role, isActive, page, size);

        model.addAttribute("users", pageUsers.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageUsers.getTotalPages());
        model.addAttribute("totalItems", pageUsers.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("isActive", isActive);

        return "admin-user-list";
    }

    // 2. Chi tiết user
    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        User user = adminUserService.getUserById(id);
        if (user == null) return "redirect:/admin/users";

        model.addAttribute("user", user);
        return "admin-user-detail";
    }

    // 3. Form cập nhật
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        User user = adminUserService.getUserById(id);
        if (user == null) return "redirect:/admin/users";

        model.addAttribute("user", user);
        return "admin-user-update";
    }

    // 4. Xử lý cập nhật
    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute("user") User formUser,
            RedirectAttributes redirectAttributes
    ) {
        User existingUser = adminUserService.getUserById(id);
        if (existingUser == null) return "redirect:/admin/users";

        // Chỉ cập nhật role và status (Admin không đổi password ở đây)
        existingUser.setUserRole(formUser.getUserRole());
        existingUser.setIsActive(formUser.getIsActive());
        existingUser.setFullName(formUser.getFullName());
        existingUser.setPhone(formUser.getPhone());
        existingUser.setAddress(formUser.getAddress());

        Map<String, String> errors = adminUserService.validateUser(existingUser);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation failed: " + errors.values().iterator().next());
            return "redirect:/admin/users/update/" + id;
        }

        try {
            adminUserService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Failed to update user!");
        }

        return "redirect:/admin/users";
    }

    // 5. Block / Unblock
    @GetMapping("/block/{id}")
    public String toggleBlock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = adminUserService.getUserById(id);
        if (user == null) return "redirect:/admin/users";

        boolean wasActive = user.getIsActive();
        adminUserService.toggleBlock(id);
        redirectAttributes.addFlashAttribute("successMessage",
                wasActive ? "User has been blocked!" : "User has been unblocked!");

        return "redirect:/admin/users";
    }
}
