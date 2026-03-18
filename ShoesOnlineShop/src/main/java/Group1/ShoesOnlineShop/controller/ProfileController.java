package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    // Giả lập tài khoản đang đăng nhập là Admin có ID = 1
    private final Long CURRENT_USER_ID = 1L; 

    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = userService.getUserById(CURRENT_USER_ID);
        if (user == null) {
            return "redirect:/home";
        }
        model.addAttribute("user", user);
        return "marketing-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("user") User user, 
            BindingResult result, 
            RedirectAttributes redirectAttributes, 
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the invalid fields!");
            return "marketing-profile";
        }

        // Báo lỗi nếu trùng Email
        if (userService.isEmailExists(user.getUserEmail(), CURRENT_USER_ID)) {
            result.rejectValue("userEmail", "error.user", "This email is already in use by another account!");
            model.addAttribute("errorMessage", "Email validation failed.");
            return "marketing-profile";
        }

        try {
            user.setUserId(CURRENT_USER_ID); 
            userService.updateUserProfile(user);
            redirectAttributes.addFlashAttribute("successMessage", "Your profile has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "System error: Could not update profile.");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match!");
            return "redirect:/profile";
        }

        boolean success = userService.changePassword(CURRENT_USER_ID, currentPassword, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect current password!");
        }

        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}