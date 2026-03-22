package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.service.AdminFinancialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/internal/admin/financials")
public class AdminFinancialController {

    @Autowired
    private AdminFinancialService adminFinancialService;

    // 1. Trang tài chính (mặc định: tháng hiện tại)
    @GetMapping
    public String showFinancials(
            Model model,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // Mặc định: từ đầu tháng tới hôm nay
        if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate == null) endDate = LocalDate.now();

        BigDecimal totalRevenue = adminFinancialService.getTotalRevenue(startDate, endDate);
        BigDecimal totalCost = adminFinancialService.getTotalCost(startDate, endDate);
        BigDecimal netProfit = adminFinancialService.getNetProfit(startDate, endDate);
        long orderCount = adminFinancialService.countOrdersInRange(startDate, endDate);
        List<Order> transactions = adminFinancialService.getTransactionLog(startDate, endDate);

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("netProfit", netProfit);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("transactions", transactions);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin-financial";
    }
}
