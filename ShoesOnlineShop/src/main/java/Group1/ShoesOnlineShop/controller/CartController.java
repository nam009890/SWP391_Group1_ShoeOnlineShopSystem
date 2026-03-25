package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Cart;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // Check if it's an OAuth2 user
            if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
                return userRepository.findByProviderId(auth.getName())
                        .or(() -> {
                            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = 
                                (org.springframework.security.oauth2.core.user.OAuth2User) auth.getPrincipal();
                            String email = oauth2User.getAttribute("email");
                            return userRepository.findByUserEmail(email);
                        })
                        .map(User::getUserName)
                        .orElse(null);
            }
            return auth.getName();
        }
        return null;
    }

    private String getSessionId(HttpSession session) {
        return session.getId();
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        String username = getUsername();
        String sessionId = getSessionId(session);

        List<Cart> cartItems = cartService.getCartItems(username, sessionId);
        
        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", total);
        return "customer-cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam("productId") Long productId,
                                                         @RequestParam("quantity") Integer quantity,
                                                         @RequestParam(value = "size", required = false) String size,
                                                         @RequestParam(value = "color", required = false) String color,
                                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (size == null || size.trim().isEmpty() || color == null || color.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select size and color before adding to cart.");
                return ResponseEntity.badRequest().body(response);
            }

            String username = getUsername();
            String sessionId = getSessionId(session);
            
            cartService.addToCart(productId, quantity, size, color, username, sessionId);
            
            response.put("success", true);
            response.put("message", "Added to cart successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(@RequestParam("cartId") Long cartId,
                                                         @RequestParam("quantity") Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            cartService.updateCartItemQuantity(cartId, quantity);
            response.put("success", true);
            response.put("message", "Cart updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/remove")
    public String removeCartItem(@RequestParam("cartId") Long cartId) {
        cartService.removeCartItem(cartId);
        return "redirect:/cart";
    }
}
