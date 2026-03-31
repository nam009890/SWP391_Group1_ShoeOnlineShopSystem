package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.service.UserService;
import Group1.ShoesOnlineShop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showCustomerLogin() {
        return "login";
    }

    @GetMapping("/internal/login")
    public String showInternalLogin() {
        return "internal-login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Generate 6-digit code
            String code = String.valueOf((int) (Math.random() * 900000) + 100000);
            userRepository.updateResetToken(email, code, java.time.LocalDateTime.now().plusMinutes(15));

            // Send Email
            emailService.sendVerificationCode(email, code);
            
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/verify-reset-code";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Email not found.");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/verify-reset-code")
    public String showVerifyCodeForm(@ModelAttribute("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "verify-reset-code";
    }

    @PostMapping("/verify-reset-code")
    public String verifyResetCode(@RequestParam("email") String email, 
                                 @RequestParam("code") String code, 
                                 RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (code.equals(user.getResetToken()) && 
                user.getResetTokenExpiry().isAfter(java.time.LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("token", code); // Using the same code as token for simplicity
                return "redirect:/reset-password";
            }
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired code.");
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/verify-reset-code";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@ModelAttribute("email") String email, 
                                       @ModelAttribute("token") String token, 
                                       Model model) {
        if (email == null || email.isEmpty() || token == null || token.isEmpty()) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/reset-password";
        }

        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (token.equals(user.getResetToken())) {
                userRepository.updatePasswordAndClearToken(email, passwordEncoder.encode(newPassword));
                redirectAttributes.addFlashAttribute("successMessage", "Password has been reset successfully.");
                return "redirect:/login";
            }
        }
        return "redirect:/forgot-password";
    }

    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerCustomer(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
}
