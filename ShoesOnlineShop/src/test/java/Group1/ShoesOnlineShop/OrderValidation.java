///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
//package Group1.ShoesOnlineShop;
//import Group1.ShoesOnlineShop.entity.Order;
//import Group1.ShoesOnlineShop.entity.OrderDetail;
//import Group1.ShoesOnlineShop.entity.Product;
//import Group1.ShoesOnlineShop.repository.OrderRepository;
//
//import java.math.BigDecimal;
///**
// *
// * @author Windows
// */
//public class OrderValidation {
//       public static String validateAndUpdateOrder(
//            Long id,
//            String phone,
//            String address,
//            String quantity,
//            String status,
//            OrderRepository orderRepository
//    ) {
//
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        // ================= PHONE =================
//        if (phone == null || phone.trim().isEmpty()) {
//            throw new IllegalArgumentException("Phone must not be empty");
//        }
//
//        if (!phone.matches("^0\\d{9}$")) {
//            throw new IllegalArgumentException("Phone must have 10 digits and start with 0");
//        }
//
//        // ================= ADDRESS =================
//        if (address == null || address.trim().isEmpty()) {
//            throw new IllegalArgumentException("Address must not be empty");
//        }
//
//        if (address.length() > 255) {
//            throw new IllegalArgumentException("Address must be less than 255 characters");
//        }
//
//        // ================= QUANTITY =================
//        if (quantity == null) {
//            throw new IllegalArgumentException("Quantity must not be null");
//        }
//
//        if (quantity <= 0) {
//            throw new IllegalArgumentException("Quantity must be positive integer");
//        }
//
//        // ================= UPDATE =================
//        order.setPhone(phone);
//        order.setShippingAddress(address);
//        order.setOrderStatus(status);
//
//        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
//
//            OrderDetail detail = order.getOrderDetails().get(0);
//            detail.setQuantity(quantity);
//
//            order.setTotalAmount(
//                    detail.getProduct().getProductPrice()
//                            .multiply(BigDecimal.valueOf(quantity))
//            );
//        }
//
//        orderRepository.save(order);
//
//        return "Update order successfully!";
//    }
//}
