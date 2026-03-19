/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.service;
import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Group1.ShoesOnlineShop.entity.OrderDetail;
import java.math.BigDecimal;
import java.util.List;
import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.service.OrderService;
/**
 *
 * @author Windows
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

  public Page<Order> getOrders(String status,
                             String keyword,
                             int page,
                             String sort) {

    Pageable pageable = PageRequest.of(page, 5, Sort.by(sort));

    if (status != null && status.isEmpty()) {
        status = null;
    }

    if (keyword != null && keyword.isEmpty()) {
        keyword = null;
    }

    return orderRepository.searchOrders(status, keyword, pageable);
}

   public void updateStatus(Long id, String status) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    order.setOrderStatus(status);

    orderRepository.save(order);
}

    public void deleteOrder(Long id) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    order.setIsActive(false);

    orderRepository.save(order);
}

public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
public String updateOrder(Long id,
                        String phone,
                        String address,
                        String status) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
// =========================
    // PHONE VALIDATION
    // =========================
    if (phone == null || phone.trim().isEmpty()) {
        throw new IllegalArgumentException("Phone numbers must be not null or empty");
    }

    if (!phone.matches("^0\\d{9}$")) {
        throw new IllegalArgumentException("Phone numbers must have 10 digits and start with 0");
    }

    // =========================
    // ADDRESS VALIDATION
    // =========================
    if (address == null || address.trim().isEmpty()) {
        throw new IllegalArgumentException("Address must be not null or empty");
    }

    if (address.length() > 255) {
        throw new IllegalArgumentException("Address must be less than 255 characters");
    }

    // Update order info
    order.setPhone(phone);
    order.setShippingAddress(address);
    order.setOrderStatus(status);

    orderRepository.save(order);
    return "Update order successfully!";
}
 
}
