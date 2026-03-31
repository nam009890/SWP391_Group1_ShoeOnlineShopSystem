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

    // === Regex Constants ===
    private static final String FULL_NAME_REGEX = "^[\\p{L}\\s]+$";
    private static final String PHONE_REGEX = "^0\\d{9}$";
    private static final String ADDRESS_FORMAT_REGEX = "^[\\p{L}0-9\\s.,/\\-#]+$";
    private static final String ADDRESS_CONTENT_REGEX = ".*[\\p{L}0-9].*";
    private static final String PASSWORD_LETTER_REGEX = ".*[a-zA-Z].*";
    private static final String PASSWORD_DIGIT_REGEX = ".*\\d.*";

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
        return "admin/admin-profile";
    }

    // === UPDATE PROFILE ===
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam(name = "fullName") String fullName,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "address", required = false) String address,
            RedirectAttributes redirectAttributes
    ) {
        Long currentAdminId = getCurrentAdminId();
        if (currentAdminId == null) return "redirect:/login";

        User admin = userService.getUserById(currentAdminId);
        if (admin == null) return "redirect:/internal/admin/home";

        // === Validate Full Name ===
        if (fullName == null || fullName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Full Name cannot be blank!");
            return "redirect:/internal/admin/profile";
        }
        String trimmedFullName = fullName.trim();
        if (trimmedFullName.length() < 2 || trimmedFullName.length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Full Name must be between 2 and 100 characters!");
            return "redirect:/internal/admin/profile";
        }
        if (!trimmedFullName.matches(FULL_NAME_REGEX)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Full Name must contain only letters and spaces, no numbers or special characters!");
            return "redirect:/internal/admin/profile";
        }

        // === Validate Phone Number ===
        if (phone != null && !phone.trim().isEmpty()) {
            String trimmedPhone = phone.trim();
            if (!trimmedPhone.matches(PHONE_REGEX)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Phone number must be exactly 10 digits and start with 0!");
                return "redirect:/internal/admin/profile";
            }
        }

        // === Validate Address ===
        if (address != null && !address.trim().isEmpty()) {
            String trimmedAddress = address.trim();
            if (trimmedAddress.length() < 10 || trimmedAddress.length() > 255) {
                redirectAttributes.addFlashAttribute("errorMessage", "Address must be between 10 and 255 characters!");
                return "redirect:/internal/admin/profile";
            }
            if (!trimmedAddress.matches(ADDRESS_FORMAT_REGEX)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Address contains invalid special characters! Allowed characters: letters, numbers, spaces, commas, dots, slashes, hyphens, and hashes.");
                return "redirect:/internal/admin/profile";
            }
            if (!trimmedAddress.matches(ADDRESS_CONTENT_REGEX)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Address must contain at least some letters or numbers, not only special characters!");
                return "redirect:/internal/admin/profile";
            }
        }

        // Email & Username: Không cho phép chỉnh sửa → giữ nguyên giá trị cũ
        admin.setFullName(trimmedFullName);
        admin.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        admin.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);

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
        // === Validate New Password ===
        if (newPassword == null || newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password must be at least 8 characters!");
            return "redirect:/internal/admin/profile";
        }
        if (!newPassword.matches(PASSWORD_LETTER_REGEX)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password must contain at least one letter!");
            return "redirect:/internal/admin/profile";
        }
        if (!newPassword.matches(PASSWORD_DIGIT_REGEX)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password must contain at least one digit!");
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

