package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Check if it's an OAuth2 user
            if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
                // Return by Provider ID (sub) first, then by email as fallback
                return userRepository.findByProviderId(auth.getName())
                        .or(() -> {
                            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = 
                                (org.springframework.security.oauth2.core.user.OAuth2User) auth.getPrincipal();
                            String email = oauth2User.getAttribute("email");
                            return userRepository.findByUserEmail(email);
                        }).orElse(null);
            }
            // Standard user
            return userRepository.findByUserName(auth.getName()).orElse(null);
        }
        return null;
    }

    // ==========================================
    // CUSTOMER PROFILE (/profile)
    // ==========================================

    @GetMapping("/profile")
    public String showCustomerProfile(Model model) {
        User user = getAuthenticatedUser();
        if (user == null || !user.getUserRole().equals("CUSTOMER")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "customer-profile";
    }

    @PostMapping("/profile/update")
    public String updateCustomerProfile(
            @Valid @ModelAttribute("user") User user, 
            BindingResult result, 
            RedirectAttributes redirectAttributes, 
            Model model) {
        
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the invalid fields!");
            return "customer-profile";
        }

        if (userService.isEmailExists(user.getUserEmail(), currentUser.getUserId())) {
            result.rejectValue("userEmail", "error.user", "This email is already in use by another account!");
            model.addAttribute("errorMessage", "Email validation failed.");
            return "customer-profile";
        }

        try {
            user.setUserId(currentUser.getUserId()); 
            userService.updateUserProfile(user);
            redirectAttributes.addFlashAttribute("successMessage", "Your profile has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Could not update profile.");
        }

        return "redirect:/profile";
    }

    @GetMapping("/profile/change-password")
    public String showCustomerChangePasswordForm(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "customer-change-password";
    }

    @PostMapping("/profile/change-password")
    public String changeCustomerPassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match!");
            return "redirect:/profile/change-password";
        }

        boolean success = userService.changePassword(currentUser.getUserId(), currentPassword, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect current password!");
        }

        return "redirect:/profile/change-password";
    }

    // ==========================================
    // INTERNAL STAFF PROFILE (/internal/profile)
    // ==========================================

    @GetMapping("/internal/profile")
    public String showInternalProfile(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/internal/login";
        }
        model.addAttribute("user", user);
        return "marketing-profile";
    }

    @PostMapping("/internal/profile/update")
    public String updateInternalProfile(
            @Valid @ModelAttribute("user") User user, 
            BindingResult result, 
            RedirectAttributes redirectAttributes, 
            Model model) {

        User currentUser = getAuthenticatedUser();
        if (currentUser == null) return "redirect:/internal/login";

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the invalid fields!");
            return "marketing-profile";
        }

        if (userService.isEmailExists(user.getUserEmail(), currentUser.getUserId())) {
            result.rejectValue("userEmail", "error.user", "This email is already in use by another account!");
            model.addAttribute("errorMessage", "Email validation failed.");
            return "marketing-profile";
        }

        try {
            user.setUserId(currentUser.getUserId()); 
            userService.updateUserProfile(user);
            redirectAttributes.addFlashAttribute("successMessage", "Your profile has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Could not update profile.");
        }

        return "redirect:/internal/profile";
    }

    @GetMapping("/internal/profile/change-password")
    public String showInternalChangePasswordForm(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/internal/login";
        model.addAttribute("user", user);
        return "marketing-change-password";
    }

    @PostMapping("/internal/profile/change-password")
    public String changeInternalPassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) return "redirect:/internal/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match!");
            return "redirect:/internal/profile/change-password";
        }

        boolean success = userService.changePassword(currentUser.getUserId(), currentPassword, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect current password!");
        }

        return "redirect:/internal/profile/change-password";
    }
}