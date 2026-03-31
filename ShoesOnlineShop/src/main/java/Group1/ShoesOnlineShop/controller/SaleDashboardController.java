package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.repository.FeedbackRepository;
import Group1.ShoesOnlineShop.service.AdminFinancialService;
import Group1.ShoesOnlineShop.service.AdminOrderService;
import Group1.ShoesOnlineShop.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/internal/sales")
public class SaleDashboardController {

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private AdminFinancialService adminFinancialService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        // Stats
        long totalOrders = adminOrderService.countAllOrders();
        long totalFeedbacks = feedbackRepository.count();
        long totalInvoices = invoiceService.getAllInvoices().size();

        // Revenue this month
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        BigDecimal monthRevenue = adminFinancialService.getTotalRevenue(startOfMonth, today);

        // Recent Orders
        List<Order> recentOrders = adminOrderService.getRecentOrders(10);

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalFeedbacks", totalFeedbacks);
        model.addAttribute("totalInvoices", totalInvoices);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("recentOrders", recentOrders);

        return "sale/sale-dashboard";
    }
}

