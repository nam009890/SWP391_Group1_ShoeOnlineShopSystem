package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.AdminFinancialService;
import Group1.ShoesOnlineShop.service.AdminOrderService;
import Group1.ShoesOnlineShop.service.AdminProductService;
import Group1.ShoesOnlineShop.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminProductService adminProductService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private AdminFinancialService adminFinancialService;

    @Autowired
    private Group1.ShoesOnlineShop.service.AdminCategoryService adminCategoryService;

    @GetMapping({"", "/", "/home"})
    public String dashboard(Model model) {
        // Stats hiện tại
        long totalProducts = adminProductService.countAllProducts();
        long totalUsers = adminUserService.countAllUsers();
        long totalOrders = adminOrderService.countAllOrders();
        long totalCategories = adminCategoryService.countAllCategories();

        // Revenue tháng này
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        BigDecimal monthRevenue = adminFinancialService.getTotalRevenue(startOfMonth, today);

        // Recent Orders (10 đơn gần nhất)
        List<Order> recentOrders = adminOrderService.getRecentOrders(10);

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("recentOrders", recentOrders);

        return "admin-home";
    }
}
