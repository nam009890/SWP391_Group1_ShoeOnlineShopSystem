package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/internal/admin/orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    // 1. Danh sách đơn hàng (view-only)
    @GetMapping
    public String listOrders(
            Model model,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<Order> pageOrders = adminOrderService.getOrders(keyword, status, page, size);

        model.addAttribute("orders", pageOrders.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageOrders.getTotalPages());
        model.addAttribute("totalItems", pageOrders.getTotalElements());
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);

        return "admin/admin-order-list";
    }

    // 2. Chi tiết đơn hàng (view-only)
    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable(name = "id") Long id, Model model) {
        Order order = adminOrderService.getOrderById(id);
        if (order == null) return "redirect:/internal/admin/orders";

        model.addAttribute("order", order);
        return "admin/admin-order-detail";
    }
}

