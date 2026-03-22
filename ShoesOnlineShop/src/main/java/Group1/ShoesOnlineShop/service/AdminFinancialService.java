package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class AdminFinancialService {

    // Estimated cost ratio (70% of revenue)
    private static final BigDecimal COST_RATIO = new BigDecimal("0.70");

    @Autowired
    private AdminOrderService adminOrderService;

    // === TOTAL REVENUE ===
    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = adminOrderService.getTotalRevenue(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    // === TOTAL COST (estimated as 70% of revenue) ===
    public BigDecimal getTotalCost(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = getTotalRevenue(startDate, endDate);
        return revenue.multiply(COST_RATIO).setScale(2, RoundingMode.HALF_UP);
    }

    // === NET PROFIT ===
    public BigDecimal getNetProfit(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = getTotalRevenue(startDate, endDate);
        BigDecimal cost = getTotalCost(startDate, endDate);
        return revenue.subtract(cost).setScale(2, RoundingMode.HALF_UP);
    }

    // === TRANSACTION LOG ===
    public List<Order> getTransactionLog(LocalDate startDate, LocalDate endDate) {
        return adminOrderService.getOrdersByDateRange(startDate, endDate);
    }

    // === COUNT ORDERS IN DATE RANGE ===
    public long countOrdersInRange(LocalDate startDate, LocalDate endDate) {
        return adminOrderService.countOrdersByDateRange(startDate, endDate);
    }
}
