package Group1.ShoesOnlineShop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFinancialServiceTest {

    @Mock
    private AdminOrderService adminOrderService;

    @InjectMocks
    private AdminFinancialService adminFinancialService;

    private final LocalDate START = LocalDate.of(2025, 1, 1);
    private final LocalDate END   = LocalDate.of(2025, 1, 31);

    // 1. REVENUE - Lấy đúng từ AdminOrderService
    @Test
    void testGetTotalRevenue_DelegatesToOrderService() {
        when(adminOrderService.getTotalRevenue(START, END)).thenReturn(new BigDecimal("5000000"));

        BigDecimal revenue = adminFinancialService.getTotalRevenue(START, END);

        assertEquals(new BigDecimal("5000000"), revenue);
        verify(adminOrderService, times(1)).getTotalRevenue(START, END);
    }

    // 2. COST - = 70% của revenue
    @Test
    void testGetTotalCost_Is70PercentOfRevenue() {
        when(adminOrderService.getTotalRevenue(START, END)).thenReturn(new BigDecimal("10000000"));

        BigDecimal cost = adminFinancialService.getTotalCost(START, END);

        assertEquals(new BigDecimal("7000000.00"), cost);
    }

    // 3. PROFIT = Revenue - Cost = 30% of revenue
    @Test
    void testGetNetProfit_Is30PercentOfRevenue() {
        when(adminOrderService.getTotalRevenue(any(), any())).thenReturn(new BigDecimal("10000000"));

        BigDecimal profit = adminFinancialService.getNetProfit(START, END);

        // 10M - 7M = 3M
        assertEquals(new BigDecimal("3000000.00"), profit);
    }

    // 4. REVENUE BẰNG 0 → COST BẰNG 0
    @Test
    void testGetTotalCost_WhenRevenueIsZero() {
        when(adminOrderService.getTotalRevenue(START, END)).thenReturn(BigDecimal.ZERO);

        BigDecimal cost = adminFinancialService.getTotalCost(START, END);

        assertEquals(BigDecimal.ZERO.setScale(2), cost);
    }

    // 5. PROFIT BẰNG 0 KHI REVENUE BẰNG 0
    @Test
    void testGetNetProfit_WhenRevenueIsZero() {
        when(adminOrderService.getTotalRevenue(any(), any())).thenReturn(BigDecimal.ZERO);

        BigDecimal profit = adminFinancialService.getNetProfit(START, END);

        assertEquals(BigDecimal.ZERO.setScale(2), profit);
    }

    // 6. COUNT ORDERS
    @Test
    void testCountOrdersInRange_DelegatesToOrderService() {
        when(adminOrderService.countOrdersByDateRange(START, END)).thenReturn(45L);

        long count = adminFinancialService.countOrdersInRange(START, END);

        assertEquals(45L, count);
    }

    // 7. TRANSACTION LOG
    @Test
    void testGetTransactionLog_DelegatesToOrderService() {
        when(adminOrderService.getOrdersByDateRange(START, END)).thenReturn(java.util.List.of());

        var list = adminFinancialService.getTransactionLog(START, END);

        assertNotNull(list);
        verify(adminOrderService, times(1)).getOrdersByDateRange(START, END);
    }

    // 8. REVENUE NULL → 0
    @Test
    void testGetTotalRevenue_NullFromOrderService_ReturnsZero() {
        when(adminOrderService.getTotalRevenue(START, END)).thenReturn(null);

        BigDecimal revenue = adminFinancialService.getTotalRevenue(START, END);

        assertEquals(BigDecimal.ZERO, revenue);
    }
}
