package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class CustomerFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addFeedback(
            @RequestParam("productId") Long productId,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment) {

        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            response.put("success", false);
            response.put("message", "You must be logged in to leave a review.");
            return ResponseEntity.status(401).body(response);
        }

        User user = userRepository.findByUserName(auth.getName()).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || product == null) {
            response.put("success", false);
            response.put("message", "Invalid user or product.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            feedbackService.saveFeedback(user, product, rating, comment);
            response.put("success", true);
            response.put("message", "Thank you for your feedback!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
