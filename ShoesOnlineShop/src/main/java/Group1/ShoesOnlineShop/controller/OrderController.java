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
@RequestMapping("/internal/orders")
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
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "orderId") String sort,
            Model model) {

        Page<Order> orders = orderService.getOrders(status, keyword, page, sort);

        model.addAttribute("orders", orders);
        model.addAttribute("statuses",
        List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"));
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

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
    public String createOrder(@RequestParam(name = "userId") Long userId,
                              @RequestParam(name = "productId") Long productId,
                              @RequestParam(name = "quantity") Integer quantity,
                              @RequestParam(name = "phone") String phone,
                              @RequestParam(name = "address") String address,
                              @RequestParam(name = "status") String status,
                              RedirectAttributes redirectAttributes) {
        try {
            String message = orderService.createOrder(userId, productId, quantity, phone, address, status);
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/internal/orders/create";
        }
        return "redirect:/internal/orders";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "status") String status) {
        orderService.updateStatus(id, status);
        return "redirect:/internal/orders";
    }

  @GetMapping("/delete/{id}")
public String delete(@PathVariable(name = "id") Long id) {
    orderService.deleteOrder(id);
    return "redirect:/internal/orders";
}
    // ====== SHOW EDIT PAGE ======
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable(name = "id") Long id, Model model) {

        Order order = orderService.findById(id);

        model.addAttribute("order", order);

        model.addAttribute("statuses",
                List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"));

        return "edit-order";
    }

    // ====== HANDLE UPDATE ======
  @PostMapping("/update")
public String updateOrder(@RequestParam(name = "id") Long id,
                          @RequestParam(name = "phone") String phone,
                          @RequestParam(name = "address") String address,
                          @RequestParam(name = "status") String status,
                          RedirectAttributes redirectAttributes) {

    try {
        String message = orderService.updateOrder(id, phone, address, status);
        redirectAttributes.addFlashAttribute("successMessage", message);

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/internal/orders/edit/" + id;
    }

    return "redirect:/internal/orders";
}
@GetMapping("/view/{id}")
public String viewOrder(@PathVariable(name = "id") Long id, Model model) {

    Order order = orderService.findById(id);

    model.addAttribute("order", order);

    return "order-detail";
}
}