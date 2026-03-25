package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/my-orders")
public class CustomerOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public CustomerOrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
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
                        }).orElse(null);
            }
            return userRepository.findByUserName(auth.getName()).orElse(null);
        }
        return null;
    }

    @GetMapping
    public String listOrders(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user.getUserId());
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);

        return "customer-order-list";
    }

    @GetMapping("/{id}")
    public String showOrderDetail(@PathVariable("id") Long orderId, Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderService.findById(orderId);
        if (order == null || !order.getUser().getUserId().equals(user.getUserId())) {
            // Prevent viewing someone else's order
            return "redirect:/my-orders";
        }

        List<Group1.ShoesOnlineShop.entity.OrderHistory> timeline = orderService.getTimeline(orderId);

        model.addAttribute("order", order);
        model.addAttribute("timeline", timeline);
        model.addAttribute("user", user);
        return "customer-order-detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable("id") Long orderId, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        
        try {
            orderService.cancelOrder(user.getUserId(), orderId);
            ra.addFlashAttribute("successMessage", "Order cancelled successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-orders/" + orderId;
    }

    @PostMapping("/{id}/reorder")
    public String reorder(@PathVariable("id") Long orderId, jakarta.servlet.http.HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        
        try {
            orderService.reorder(user.getUserId(), orderId, session.getId());
            ra.addFlashAttribute("successMessage", "Items from previous order added to your cart.");
            return "redirect:/cart";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/my-orders/" + orderId;
        }
    }
}
