package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.repository.AdminOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private AdminOrderRepository adminOrderRepository;

    @InjectMocks
    private AdminOrderService adminOrderService;

    // 1. GET ORDER BY ID - Tìm thấy
    @Test
    void testGetOrderById_Found() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setTotalAmount(new BigDecimal("500000"));

        when(adminOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = adminOrderService.getOrderById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
    }

    // 2. GET ORDER BY ID - Không tìm thấy
    @Test
    void testGetOrderById_NotFound() {
        when(adminOrderRepository.findById(999L)).thenReturn(Optional.empty());
        Order result = adminOrderService.getOrderById(999L);
        assertNull(result);
    }

    // 3. GET REVENUE - Khi có dữ liệu
    @Test
    void testGetTotalRevenue_WithData() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        when(adminOrderRepository.sumRevenueByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("10000000"));

        BigDecimal revenue = adminOrderService.getTotalRevenue(start, end);
        assertEquals(new BigDecimal("10000000"), revenue);
    }

    // 4. GET REVENUE - Không có đơn hàng (null → 0)
    @Test
    void testGetTotalRevenue_NoOrders_ReturnsZero() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        when(adminOrderRepository.sumRevenueByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO);

        BigDecimal revenue = adminOrderService.getTotalRevenue(start, end);
        assertEquals(BigDecimal.ZERO, revenue);
    }

    // 5. COUNT ALL ORDERS
    @Test
    void testCountAllOrders() {
        when(adminOrderRepository.countAllOrders()).thenReturn(150L);
        assertEquals(150L, adminOrderService.countAllOrders());
    }

    // 6. COUNT IN DATE RANGE
    @Test
    void testCountOrdersByDateRange() {
        LocalDate start = LocalDate.of(2025, 3, 1);
        LocalDate end = LocalDate.of(2025, 3, 31);

        when(adminOrderRepository.countOrdersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(25L);

        long count = adminOrderService.countOrdersByDateRange(start, end);
        assertEquals(25L, count);
    }

    // 7. GET ORDERS BY DATE RANGE
    @Test
    void testGetOrdersByDateRange_ReturnsList() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        Order o1 = new Order();
        o1.setOrderId(1L);
        Order o2 = new Order();
        o2.setOrderId(2L);

        when(adminOrderRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(o1, o2));

        List<Order> orders = adminOrderService.getOrdersByDateRange(start, end);
        assertEquals(2, orders.size());
    }

    // 8. GET LIST WITH FILTER - Gọi repository đúng
    @Test
    @SuppressWarnings("unchecked")
    void testGetOrders_WithStatusFilter() {
        when(adminOrderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        var page = adminOrderService.getOrders(null, "PENDING", 1, 10);
        assertNotNull(page);
        verify(adminOrderRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}
