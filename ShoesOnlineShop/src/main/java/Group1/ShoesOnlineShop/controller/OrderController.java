package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import Group1.ShoesOnlineShop.entity.OrderStatus;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@CrossOrigin
public class OrderController {

        private final OrderService orderService;
          public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "orderId") String sort,
            Model model) {

        Page<Order> orders = orderService.getOrders(status, keyword, page, sort);

        model.addAttribute("orders", orders);
        model.addAttribute("statuses",
        List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"));
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword);

       return "order-list";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long id,
                               @RequestParam String status) {
        orderService.updateStatus(id, status);
        return "redirect:/orders";
    }

  @GetMapping("/delete/{id}")
public String delete(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return "redirect:/orders";
}
   // ====== SHOW EDIT PAGE ======
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Order order = orderService.findById(id);

        model.addAttribute("order", order);

        model.addAttribute("statuses",
                List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"));

        return "edit-order";
    }

    // ====== HANDLE UPDATE ======
  @PostMapping("/update")
public String updateOrder(@RequestParam Long id,
                          @RequestParam String phone,
                          @RequestParam String address,
                          @RequestParam Integer quantity,
                          @RequestParam String status,
                          RedirectAttributes redirectAttributes) {

    try {
        String message = orderService.updateOrder(id, phone, address, quantity, status);
        redirectAttributes.addFlashAttribute("successMessage", message);

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/orders/edit/" + id;
    }

    return "redirect:/orders";
}
}