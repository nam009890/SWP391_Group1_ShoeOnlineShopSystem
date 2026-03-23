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
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/MyOrder")
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

    @GetMapping("/list")
    public String showOrderHistory(Model model) {
        User user = getAuthenticatedUser();
        if (user == null || !user.getUserRole().equals("CUSTOMER")) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user.getUserId());
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);

        return "customer-order-list";
    }

    @GetMapping("/detail/{id}")
    public String showOrderDetail(@PathVariable("id") Long orderId, Model model) {
        User user = getAuthenticatedUser();
        if (user == null || !user.getUserRole().equals("CUSTOMER")) {
            return "redirect:/login";
        }

        Order order = orderService.findById(orderId);
        if (order == null || !order.getUser().getUserId().equals(user.getUserId())) {
            // Prevent viewing someone else's order
            return "redirect:/MyOrder/list";
        }

        model.addAttribute("order", order);
        model.addAttribute("user", user);
        return "customer-order-detail";
    }
}
