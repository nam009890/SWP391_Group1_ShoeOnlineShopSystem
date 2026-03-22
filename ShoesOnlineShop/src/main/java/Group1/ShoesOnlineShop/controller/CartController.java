package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Cart;
import Group1.ShoesOnlineShop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
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
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            String username = getUsername();
            String sessionId = getSessionId(session);
            
            cartService.addToCart(productId, quantity, username, sessionId);
            redirectAttributes.addFlashAttribute("successMessage", "Added to cart successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/products");
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("cartId") Long cartId,
                             @RequestParam("quantity") Integer quantity,
                             RedirectAttributes redirectAttributes) {
        try {
            cartService.updateCartItemQuantity(cartId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeCartItem(@RequestParam("cartId") Long cartId) {
        cartService.removeCartItem(cartId);
        return "redirect:/cart";
    }
}
