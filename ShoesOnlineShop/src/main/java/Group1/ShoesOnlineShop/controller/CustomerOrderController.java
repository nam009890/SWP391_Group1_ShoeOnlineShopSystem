package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.CustomerOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService customerOrderService;

    @GetMapping
    public String listOrders(HttpSession session, Model model, 
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "size", defaultValue = "5") int size) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 1L; // Mock user cho mục đích demo
        }
        
        Page<Order> orderPage = customerOrderService.getOrdersByUser(userId, page, size);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        
        return "customer-order-list";
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable(name = "id") Long id, Model model) {
        Order order = customerOrderService.getOrderById(id);
        if (order == null) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        return "customer-order-detail";
    }
}
