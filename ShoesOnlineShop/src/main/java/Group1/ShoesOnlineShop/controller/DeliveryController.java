package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Delivery;
import Group1.ShoesOnlineShop.service.DeliveryService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/internal/deliveries")
@CrossOrigin
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DeliveryController(DeliveryService deliveryService, OrderRepository orderRepository, UserRepository userRepository) {
        this.deliveryService = deliveryService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listDeliveries(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "deliveryId") String sort,
            Model model) {

        Page<Delivery> deliveries = deliveryService.getDeliveries(status, keyword, page, sort);

        model.addAttribute("deliveries", deliveries);
        model.addAttribute("statuses",
                List.of("PENDING", "ASSIGNED", "PICKED_UP", "DELIVERING", "DELIVERED", "FAILED"));
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return "sale/delivery-list";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "status") String status,
                               RedirectAttributes redirectAttributes) {
        try {
            deliveryService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Delivery status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update delivery: " + e.getMessage());
        }
        return "redirect:/internal/deliveries";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<Order> unassignedOrders = orderRepository.findUnassignedOrders();
        List<User> shippers = userRepository.findByUserRole("SHIPPER");
        model.addAttribute("orders", unassignedOrders);
        model.addAttribute("shippers", shippers);
        return "sale/delivery-create";
    }

    @PostMapping("/create")
    public String createDelivery(@RequestParam(name="orderId") Long orderId,
                                 @RequestParam(name="shipperId", required=false) Long shipperId,
                                 @RequestParam(name="shippingFee", required=false) java.math.BigDecimal shippingFee,
                                 @RequestParam(name="note", required=false) String note,
                                 RedirectAttributes redirectAttributes) {
        try {
            deliveryService.assignShipperToOrder(orderId, shipperId, shippingFee, note);
            redirectAttributes.addFlashAttribute("successMessage", "Created delivery successfully!");
        } catch(Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/internal/deliveries";
    }

    @GetMapping("/delete/{id}")
    public String deleteDelivery(@PathVariable(name="id") Long id, RedirectAttributes redirectAttributes) {
        try {
            deliveryService.deleteDelivery(id);
            redirectAttributes.addFlashAttribute("successMessage", "Deleted delivery successfully!");
        } catch(Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete: " + e.getMessage());
        }
        return "redirect:/internal/deliveries";
    }

    @GetMapping("/view/{id}")
    public String viewDelivery(@PathVariable(name="id") Long id, Model model) {
        Delivery delivery = deliveryService.findById(id);
        model.addAttribute("delivery", delivery);
        model.addAttribute("order", delivery.getOrder());
        return "sale/delivery-detail";
    }
}
