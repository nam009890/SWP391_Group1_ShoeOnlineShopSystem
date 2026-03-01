package Group1.ShoesOnlineShop;

import Group1.ShoesOnlineShop.entity.*;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import Group1.ShoesOnlineShop.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderUpdateTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Fake Product
        Product product = new Product();
        product.setProductPrice(BigDecimal.valueOf(100));

        // Fake OrderDetail
        OrderDetail detail = new OrderDetail();
        detail.setProduct(product);
        detail.setQuantity(1);

        // Fake Order
        order = new Order();
        order.setOrderId(1L);
        order.setOrderDetails(new ArrayList<>(List.of(detail)));
    }

    // ================= SUCCESS =================
    @Test
    void testUpdateOrderSuccess() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        String result = orderService.updateOrder(
                1L,
                "0123456789",
                "Ha Noi",
                2,
                "CONFIRMED"
        );

        assertEquals("Update order successfully!", result);
        assertEquals(2, order.getOrderDetails().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(200), order.getTotalAmount());

        verify(orderRepository, times(1)).save(order);
    }

    // ================= PHONE Empty =================
    @Test
    void testPhoneEmpty() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "  ",
                                "HaNoi",
                                2,
                                "CONFIRMED"
                        )
                );

        assertEquals("Phone numbers must be not null or empty", ex.getMessage());
    }
    
    // ================= PHONE Null =================
    @Test
    void testPhoneNull() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                null,
                                "HaNoi",
                                2,
                                "CONFIRMED"
                        )
                );

        assertEquals("Phone numbers must be not null or empty", ex.getMessage());
    }
    
    // ================= PHONE Wrong Format =================
    @Test
    void testPhoneWrongFormat() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "123456",
                                "HaNoi",
                                2,
                                "CONFIRMED"
                        )
                );

        assertEquals("Phone numbers must have 10 digits and start with 0", ex.getMessage());
    }
    
    
    // ================= ADRESS Empty =================
    @Test
    void testAdressEmpty() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "0123456789",
                                "  ",
                                2,
                                "CONFIRMED"
                        )
                );

        assertEquals("Address must be not null or empty", ex.getMessage());
    }
    
    // ================= ADRESS Null =================
    @Test
    void testAdressNull() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "0123456789",
                                null,
                                2,
                                "CONFIRMED"
                        )
                );

        assertEquals("Address must be not null or empty", ex.getMessage());
    }
    
    // ================= ADDRESS > 255 =================
    @Test
    void testAddressMoreThan255() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        String longAddress = "a".repeat(256);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "0123456789",
                                longAddress,
                                2,
                                "CONFIRMED"
                        )
                );

        assertEquals("Address must be less than 255 characters", ex.getMessage());
    }
    
    // ================= QUANTITY Less Than 0 =================
    @Test
    void testQuantityLessThan0() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "0123456789",
                                "Ha Noi",
                                0,
                                "CONFIRMED"
                        )
                );

        assertEquals("Quantity must be positive integer", ex.getMessage());
    }

    // ================= QUANTITY NEGATIVE =================
    @Test
    void testQuantityNegative() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "0123456789",
                                "Ha Noi",
                                -1,
                                "CONFIRMED"
                        )
                );

        assertEquals("Quantity must be positive integer", ex.getMessage());
    }
    
    // ================= QUANTITY Null =================
    @Test
    void testQuantityNull() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () ->
                        orderService.updateOrder(
                                1L,
                                "0123456789",
                                "Ha Noi",
                                null,
                                "CONFIRMED"
                        )
                );

        assertEquals("Quantity must be not null or empty", ex.getMessage());
    }

    

    // ================= ORDER NOT FOUND =================
    @Test
    void testOrderNotFound() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                orderService.updateOrder(
                        1L,
                        "0123456789",
                        "Ha Noi",
                        2,
                        "CONFIRMED"
                )
        );
    }
}