package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import Group1.ShoesOnlineShop.security.CustomUserDetails;

@Controller
@RequestMapping("/internal/admin/profile")
public class AdminProfileController {

    @Autowired
    private UserService userService;


    private Long getCurrentAdminId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getUserId();
        }
        return null;
    }

    // === VIEW PROFILE ===
    @GetMapping
    public String showProfile(Model model) {
        Long currentAdminId = getCurrentAdminId();
        if (currentAdminId == null) return "redirect:/login";

        User admin = userService.getUserById(currentAdminId);
        if (admin == null) return "redirect:/internal/admin/home";
        model.addAttribute("admin", admin);
        return "admin-profile";
    }

    // === UPDATE PROFILE ===
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam(name = "fullName") String fullName,
            @RequestParam(name = "userEmail") String userEmail,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "address", required = false) String address,
            RedirectAttributes redirectAttributes
    ) {
        Long currentAdminId = getCurrentAdminId();
        if (currentAdminId == null) return "redirect:/login";

        User admin = userService.getUserById(currentAdminId);
        if (admin == null) return "redirect:/internal/admin/home";

        // Validate
        if (fullName == null || fullName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Full Name cannot be blank!");
            return "redirect:/internal/admin/profile";
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email cannot be blank!");
            return "redirect:/internal/admin/profile";
        }

        // Check duplicate email
        if (userService.isEmailExists(userEmail, currentAdminId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "This email is already in use by another account!");
            return "redirect:/internal/admin/profile";
        }

        admin.setFullName(fullName.trim());
        admin.setUserEmail(userEmail.trim());
        admin.setPhone(phone != null ? phone.trim() : null);
        admin.setAddress(address != null ? address.trim() : null);

        try {
            userService.updateUserProfile(admin);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Could not update profile.");
        }

        return "redirect:/internal/admin/profile";
    }

    // === CHANGE PASSWORD ===
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam(name = "currentPassword") String currentPassword,
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        if (newPassword == null || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password must be at least 6 characters!");
            return "redirect:/internal/admin/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match!");
            return "redirect:/internal/admin/profile";
        }

        Long currentAdminId = getCurrentAdminId();
        if (currentAdminId == null) return "redirect:/login";

        String error = userService.changePassword(currentAdminId, currentPassword, newPassword);
        if (error == null) {
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", error);
        }

        return "redirect:/internal/admin/profile";
    }
}
