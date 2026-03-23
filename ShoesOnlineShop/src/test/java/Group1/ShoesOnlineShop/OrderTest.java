///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
//package Group1.ShoesOnlineShop;
//import Group1.ShoesOnlineShop.entity.Order;
//import Group1.ShoesOnlineShop.entity.OrderDetail;
//import Group1.ShoesOnlineShop.entity.Product;
//import Group1.ShoesOnlineShop.service.OrderService;
//import Group1.ShoesOnlineShop.repository.OrderRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;    
///**
// *
// * @author Windows
// */
//public class OrderTest {
// private OrderRepository mockRepository;
//    private Order order;
//
//    @BeforeEach
//    void setup() {
//
//        mockRepository = mock(OrderRepository.class);
//
//        Product product = new Product();
//        product.setProductPrice(BigDecimal.valueOf(100));
//
//        OrderDetail detail = new OrderDetail();
//        detail.setProduct(product);
//        detail.setQuantity(1);
//
//        order = new Order();
//        order.setOrderId(1L);
//        order.setOrderDetails(new ArrayList<>(List.of(detail)));
//
//        when(mockRepository.findById(1L))
//                .thenReturn(Optional.of(order));
//    }
//
//    // ================= SUCCESS =================
//
//    @Test
//    void testUpdateOrderSuccess() {
//
//        String result = OrderValidation.validateAndUpdateOrder(
//                1L,
//                "0123456789",
//                "Ha Noi",
//                "2",
//                "CONFIRMED",
//                mockRepository
//        );
//
//        assertEquals("Update order successfully!", result);
//        assertEquals(2, order.getOrderDetails().get(0).getQuantity());
//        assertEquals(BigDecimal.valueOf(200), order.getTotalAmount());
//
//        verify(mockRepository, times(1)).save(order);
//    }
//
//    // ================= PHONE EMPTY =================
//
//    @Test
//    void testPhoneEmpty() {
//
//        IllegalArgumentException ex =
//                assertThrows(IllegalArgumentException.class, () ->
//                        OrderValidation.validateAndUpdateOrder(
//                                1L,
//                                " ",
//                                "Ha Noi",
//                                "2",
//                                "CONFIRMED",
//                                mockRepository
//                        )
//                );
//
//        assertEquals("Phone must not be empty", ex.getMessage());
//    }
//    
//    // ================= PHONE Null =================
//
//    @Test
//    void testPhoneNull() {
//
//        IllegalArgumentException ex =
//                assertThrows(IllegalArgumentException.class, () ->
//                        OrderValidation.validateAndUpdateOrder(
//                                1L,
//                                "",
//                                "Ha Noi",
//                                "2",
//                                "CONFIRMED",
//                                mockRepository
//                        )
//                );
//
//        assertEquals("Phone must not be empty", ex.getMessage());
//    }
//    
//    // ================= PHONE Wrong Format =================
//
//    @Test
//    void testPhoneWrongFormat() {
//
//        IllegalArgumentException ex =
//                assertThrows(IllegalArgumentException.class, () ->
//                        OrderValidation.validateAndUpdateOrder(
//                                1L,
//                                "123",
//                                "Ha Noi",
//                                "2",
//                                "CONFIRMED",
//                                mockRepository
//                        )
//                );
//
//        assertEquals("Phone must have 10 digits and start with 0", ex.getMessage());
//    }
//    
//    // ================= ADRESS EMPTY =================
//
//    @Test
//    void testAdressEmpty() {
//
//        IllegalArgumentException ex =
//                assertThrows(IllegalArgumentException.class, () ->
//                        OrderValidation.validateAndUpdateOrder(
//                                1L,
//                                "0123456789",
//                                "    ",
//                                "2",
//                                "CONFIRMED",
//                                mockRepository
//                        )
//                );
//
//        assertEquals("Address must not be empty", ex.getMessage());
//    }
//    
//    // ================= ADRESS NULL =================
//
//    @Test
//    void testAdressNull() {
//
//        IllegalArgumentException ex =
//                assertThrows(IllegalArgumentException.class, () ->
//                        OrderValidation.validateAndUpdateOrder(
//                                1L,
//                                "0123456789",
//                                "",
//                                "2",
//                                "CONFIRMED",
//                                mockRepository
//                        )
//                );
//
//        assertEquals("Address must not be empty", ex.getMessage());
//    }
//    
//    // ================= ADDRESS MORE THAN 255 =================
//
//@Test
//void testAddressMoreThan255() {
//
//    // Tạo chuỗi 256 ký tự
//    String longAddress = "a".repeat(256);
//
//    IllegalArgumentException ex =
//            assertThrows(IllegalArgumentException.class, () ->
//                    OrderValidation.validateAndUpdateOrder(
//                            1L,
//                            "0123456789",
//                            longAddress,   // >255 ký tự
//                            "2",
//                            "CONFIRMED",
//                            mockRepository
//                    )
//            );
//
//    assertEquals("Address must be less than 255 characters", ex.getMessage());
//}
//    
//    
//    
//
//    // ================= QUANTITY Null =================
//
//    @Test
//    void testQuantityNull() {
//        IllegalArgumentException ex =
//        assertThrows(IllegalArgumentException.class, () ->
//                OrderValidation.validateAndUpdateOrder(
//                        1L,
//                        "0123456789",
//                        "Ha Noi",
//                        null,
//                        "CONFIRMED",
//                        mockRepository
//                )
//        );
//        assertEquals("Quantity must not be null", ex.getMessage());
//    }
//    
//     // ================= QUANTITY NEGATIVE =================
//
//    @Test
//    void testQuantityNegative() {
//        IllegalArgumentException ex =
//        assertThrows(IllegalArgumentException.class, () ->
//                OrderValidation.validateAndUpdateOrder(
//                        1L,
//                        "0123456789",
//                        "Ha Noi",
//                        "-1",
//                        "CONFIRMED",
//                        mockRepository
//                )
//        );
//        assertEquals("Quantity must be positive integer", ex.getMessage());
//    }
//       
//
//    // ================= ORDER NOT FOUND =================
//
//    @Test
//    void testOrderNotFound() {
//
//        when(mockRepository.findById(2L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class, () ->
//                OrderValidation.validateAndUpdateOrder(
//                        2L,
//                        "0123456789",
//                        "Ha Noi",
//                        "2",
//                        "CONFIRMED",
//                        mockRepository
//                )
//        );
//    }
//}
