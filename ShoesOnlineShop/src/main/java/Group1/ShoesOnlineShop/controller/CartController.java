package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Cart;
import Group1.ShoesOnlineShop.service.CartService;
import Group1.ShoesOnlineShop.service.CustomerOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private CustomerOrderService customerOrderService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        String sessionId = session.getId();
        // Giả sử userId lấy từ session nếu đã đăng nhập, ở đây demo dùng sessionId
        Long userId = (Long) session.getAttribute("userId");
        
        List<Cart> cartItems = cartService.getCartItems(userId, sessionId);
        model.addAttribute("cartItems", cartItems);
        
        BigDecimal total = BigDecimal.ZERO;
        for (Cart item : cartItems) {
            total = total.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        model.addAttribute("total", total);
        
        return "customer-cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") Integer quantity, HttpSession session, RedirectAttributes redirectAttributes) {
        String sessionId = session.getId();
        Long userId = (Long) session.getAttribute("userId");
        
        cartService.addToCart(productId, quantity, userId, sessionId);
        redirectAttributes.addFlashAttribute("message", "Product added to cart!");
        return "redirect:/products/detail/" + productId;
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long cartId, @RequestParam Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{cartId}")
    public String removeFromCart(@PathVariable Long cartId) {
        cartService.removeFromCart(cartId);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String shippingAddress, @RequestParam String phone, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!phone.matches("^[0-9]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại chỉ được chứa số.");
            return "redirect:/cart";
        }
        if (!shippingAddress.matches("^[\\p{L}0-9\\s.,\\-]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa chỉ nhà chỉ được chứa chữ, số, dấu phẩy, dấu chấm và dấu gạch ngang.");
            return "redirect:/cart";
        }
        
        String sessionId = session.getId();
        // Yêu cầu đăng nhập, tạm thời mock userId = 1 nếu null để test. Thực tế nên redirect to login
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L; // Mock user
        }
        
        customerOrderService.placeOrder(userId, sessionId, shippingAddress, phone);
        redirectAttributes.addFlashAttribute("message", "Order placed successfully!");
        return "redirect:/orders";
    }
}
