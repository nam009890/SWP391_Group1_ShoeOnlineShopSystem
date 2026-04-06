package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Delivery;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.service.DeliveryService;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/internal/shipper/deliveries")
public class ShipperController {

    private final DeliveryService deliveryService;
    private final UserRepository userRepository;

    public ShipperController(DeliveryService deliveryService, UserRepository userRepository) {
        this.deliveryService = deliveryService;
        this.userRepository = userRepository;
    }

    private User getLoggedInShipper(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByUserName(principal.getName()).orElse(null);
    }

    @GetMapping
    public String listMyDeliveries(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "deliveryId") String sort,
            Principal principal,
            Model model) {

        User shipper = getLoggedInShipper(principal);
        if (shipper == null) return "redirect:/internal/login";

        Page<Delivery> deliveries = deliveryService.getDeliveriesByShipper(shipper.getUserId(), status, keyword, page, sort);

        model.addAttribute("deliveries", deliveries);
        model.addAttribute("statuses", List.of("ASSIGNED", "PICKED_UP", "DELIVERING", "DELIVERED", "FAILED"));
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return "shipper/delivery-list";
    }

    @GetMapping("/view/{id}")
    public String viewDelivery(@PathVariable(name="id") Long id, Principal principal, Model model) {
        User shipper = getLoggedInShipper(principal);
        if (shipper == null) return "redirect:/internal/login";

        Delivery delivery = deliveryService.findById(id);
        if (!delivery.getShipper().getUserId().equals(shipper.getUserId())) {
            return "redirect:/internal/shipper/deliveries";
        }

        model.addAttribute("delivery", delivery);
        model.addAttribute("order", delivery.getOrder());
        return "shipper/delivery-detail";
    }

    @GetMapping("/available")
    public String listAvailableDeliveries(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            Principal principal,
            Model model) {

        User shipper = getLoggedInShipper(principal);
        if (shipper == null) return "redirect:/internal/login";

        Page<Delivery> deliveries = deliveryService.getAvailableDeliveries(keyword, page, sort);

        model.addAttribute("deliveries", deliveries);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return "shipper/available-deliveries";
    }

    @PostMapping("/accept")
    public String acceptDelivery(@RequestParam(name = "id") Long id,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        User shipper = getLoggedInShipper(principal);
        if (shipper == null) return "redirect:/internal/login";

        try {
            deliveryService.acceptDelivery(id, shipper.getUserId());
            redirectAttributes.addFlashAttribute("successMessage", "Accepted delivery successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to accept delivery: " + e.getMessage());
        }
        return "redirect:/internal/shipper/deliveries/available";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "status") String status,
                               @RequestParam(name = "proofImage", required = false) MultipartFile proofImage,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        User shipper = getLoggedInShipper(principal);
        if (shipper == null) return "redirect:/internal/login";

        Delivery delivery = deliveryService.findById(id);
        if (!delivery.getShipper().getUserId().equals(shipper.getUserId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not authorized to update this delivery.");
            return "redirect:/internal/shipper/deliveries";
        }

        try {
            if ("DELIVERED".equals(status)) {
                deliveryService.updateStatusWithImage(id, status, proofImage);
            } else {
                deliveryService.updateStatus(id, status);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Delivery status updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update delivery: " + e.getMessage());
        }
        return "redirect:/internal/shipper/deliveries/view/" + id;
    }
}
