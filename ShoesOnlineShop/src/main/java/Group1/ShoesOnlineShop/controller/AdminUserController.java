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
@RequestMapping("/internal/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // 1. Danh sách users
    @GetMapping
    public String listUsers(
            Model model,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "8") int size
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
    public String showDetail(@PathVariable(name = "id") Long id, Model model) {
        User user = adminUserService.getUserById(id);
        if (user == null) return "redirect:/internal/admin/users";

        model.addAttribute("user", user);
        return "admin-user-detail";
    }

    // 3. Form cập nhật
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable(name = "id") Long id, Model model) {
        User user = adminUserService.getUserById(id);
        if (user == null) return "redirect:/internal/admin/users";

        model.addAttribute("user", user);
        return "admin-user-update";
    }

    // 4. Xử lý cập nhật
    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable(name = "id") Long id,
            @ModelAttribute("user") User formUser,
            RedirectAttributes redirectAttributes
    ) {
        User existingUser = adminUserService.getUserById(id);
        if (existingUser == null) return "redirect:/internal/admin/users";

        // Chỉ cập nhật role và status (Admin không đổi password ở đây)
        // Prevent assigning the ADMIN role to any user
        if ("ADMIN".equalsIgnoreCase(formUser.getUserRole()) && !"ADMIN".equalsIgnoreCase(existingUser.getUserRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed: Cannot assign ADMIN role to another user!");
            return "redirect:/internal/admin/users/update/" + id;
        }

        existingUser.setUserRole(formUser.getUserRole());
        existingUser.setIsActive(formUser.getIsActive());
        existingUser.setFullName(formUser.getFullName());
        // existingUser.setPhone(formUser.getPhone()); // Admin không được sửa phone theo yêu cầu
        existingUser.setAddress(formUser.getAddress());

        Map<String, String> errors = adminUserService.validateUser(existingUser);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation failed: " + errors.values().iterator().next());
            return "redirect:/internal/admin/users/update/" + id;
        }

        try {
            adminUserService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Failed to update user!");
        }

        return "redirect:/internal/admin/users";
    }

    // 5. Block / Unblock
    @GetMapping("/block/{id}")
    public String toggleBlock(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        User user = adminUserService.getUserById(id);
        if (user == null) return "redirect:/internal/admin/users";

        boolean wasActive = user.getIsActive();
        adminUserService.toggleBlock(id);
        redirectAttributes.addFlashAttribute("successMessage",
                wasActive ? "User has been blocked!" : "User has been unblocked!");

        return "redirect:/internal/admin/users";
    }

    // 6. Form tạo mới user
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "admin-user-create";
    }

    // 7. Xử lý tạo mới
    @PostMapping("/save")
    public String saveNewUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        user.setIsActive(true);
        if (user.getUserRole() == null || user.getUserRole().trim().isEmpty()) {
            user.setUserRole("CUSTOMER");
        } else if ("ADMIN".equalsIgnoreCase(user.getUserRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed: Cannot assign ADMIN role to a new user!");
            return "redirect:/internal/admin/users/create";
        }

        Map<String, String> errors = adminUserService.validateUser(user);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed: " + errors.values().iterator().next());
            return "redirect:/internal/admin/users/create";
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        try {
            adminUserService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User '" + user.getUserName() + "' created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Failed to create user!");
            return "redirect:/internal/admin/users/create";
        }

        return "redirect:/internal/admin/users";
    }
}
