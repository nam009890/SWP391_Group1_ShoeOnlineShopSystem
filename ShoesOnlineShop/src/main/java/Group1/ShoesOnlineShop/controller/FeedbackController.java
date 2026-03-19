package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private Group1.ShoesOnlineShop.repository.UserRepository userRepository;

    @GetMapping
    public String index(HttpSession session) {
        // Try getting user from session (both patterns)
        Group1.ShoesOnlineShop.entity.User user = (Group1.ShoesOnlineShop.entity.User) session.getAttribute("loggedInUser");
        Long userId = (Long) session.getAttribute("userId");

        if (user == null && userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        if (user != null) {
            String role = user.getUserRole();
            if (role != null && (role.equalsIgnoreCase("SALES_STAFF") || 
                                role.equalsIgnoreCase("MARKETING") || 
                                role.equalsIgnoreCase("ADMIN") ||
                                role.equalsIgnoreCase("Staff"))) {
                return "redirect:/staff/feedbacks";
            }
        }
        return "redirect:/products";
    }

    @GetMapping("/{orderId}/{productId}")
    public String showFeedbackForm(@PathVariable Long orderId, @PathVariable Long productId, Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("productId", productId);
        return "customer-feedback";
    }

    @PostMapping("/submit")
    public String submitFeedback(
            @RequestParam Long orderId, 
            @RequestParam Long productId,
            @RequestParam Integer rating,
            @RequestParam String comments,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
            
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L; // Mock user
        }
        
        // Kiểm tra xem đã gửi feedback chưa
        if (feedbackService.hasUserSubmittedFeedbackForOrder(orderId, productId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already provided feedback for this product in this order.");
            return "redirect:/orders/detail/" + orderId;
        }

        feedbackService.submitFeedback(userId, productId, orderId, rating, comments);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your feedback has been submitted.");
        
        return "redirect:/orders/detail/" + orderId;
    }
}
