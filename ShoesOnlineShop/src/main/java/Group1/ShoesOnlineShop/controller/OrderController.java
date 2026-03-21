package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.OrderService;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.entity.Product;
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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderService orderService, UserRepository userRepository, ProductRepository productRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
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

    // ====== SHOW CREATE PAGE ======
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("statuses",
                List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"));

        return "create-order";
    }

    // ====== HANDLE CREATE ======
    @PostMapping("/create")
    public String createOrder(@RequestParam Long userId,
                              @RequestParam Long productId,
                              @RequestParam Integer quantity,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam String status,
                              RedirectAttributes redirectAttributes) {
        try {
            String message = orderService.createOrder(userId, productId, quantity, phone, address, status);
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/orders/create";
        }
        return "redirect:/orders";
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
@GetMapping("/view/{id}")
public String viewOrder(@PathVariable Long id, Model model) {

    Order order = orderService.findById(id);

    model.addAttribute("order", order);

    return "order-detail";
}
}