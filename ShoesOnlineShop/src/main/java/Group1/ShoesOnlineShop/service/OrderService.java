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
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.service.OrderService;
/**
 *
 * @author Windows
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
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

   public List<Order> getOrdersByUser(Long userId) {
       return orderRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
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
                        Integer quantity,
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

    // =========================
    // QUANTITY VALIDATION
    // =========================
    if (quantity == null) {
        throw new IllegalArgumentException("Quantity must be not null or empty");
    }

    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive integer");
    }
    // Update order info
    order.setPhone(phone);
    order.setShippingAddress(address);
order.setOrderStatus(status);
    // Update quantity trong OrderDetail
    if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {

        OrderDetail detail = order.getOrderDetails().get(0);
        detail.setQuantity(quantity);

        // Nếu có tính lại tổng tiền
        order.setTotalAmount(
                detail.getProduct().getPrice().multiply(
                        BigDecimal.valueOf(quantity)
                )
        );
    }

    orderRepository.save(order);
    return "Update order successfully!";
}

@Transactional
public String createOrder(Long userId, Long productId, Integer quantity, String phone, String address, String status) {
    // validations
    if (phone == null || phone.trim().isEmpty()) {
        throw new IllegalArgumentException("Phone numbers must be not null or empty");
    }
    if (!phone.matches("^0\\d{9}$")) {
        throw new IllegalArgumentException("Phone numbers must have 10 digits and start with 0");
    }
    if (address == null || address.trim().isEmpty()) {
        throw new IllegalArgumentException("Address must be not null or empty");
    }
    if (address.length() > 255) {
        throw new IllegalArgumentException("Address must be less than 255 characters");
    }
    if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive integer");
    }

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

    Order order = new Order();
    order.setUser(user);
    order.setPhone(phone);
    order.setShippingAddress(address);
    order.setOrderStatus(status);
    order.setPaymentStatus("PENDING");
    order.setIsActive(true);

    BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    order.setTotalAmount(totalAmount);

    OrderDetail detail = new OrderDetail();
    detail.setOrder(order);
    detail.setProduct(product);
    detail.setQuantity(quantity);
    detail.setUnitPrice(product.getPrice());
    detail.setSubtotal(totalAmount);

    order.setOrderDetails(java.util.Collections.singletonList(detail));

    orderRepository.save(order);
    return "Create order successfully!";
}

@Transactional
public Order createOrderFromCart(User user, java.util.List<Group1.ShoesOnlineShop.entity.Cart> cartItems, String phone, String address, String paymentMethod) {
    if (phone == null || phone.trim().isEmpty() || !phone.matches("^0\\d{9}$")) {
        throw new IllegalArgumentException("Invalid phone number");
    }
    if (address == null || address.trim().isEmpty() || address.length() > 255) {
        throw new IllegalArgumentException("Invalid address");
    }
    if (cartItems == null || cartItems.isEmpty()) {
        throw new IllegalArgumentException("Cart is empty");
    }

    Order order = new Order();
    order.setUser(user);
    order.setPhone(phone);
    order.setShippingAddress(address);
    order.setOrderStatus("PENDING");
    order.setPaymentStatus("PENDING");
    order.setIsActive(true);

    BigDecimal totalAmount = BigDecimal.ZERO;
    List<OrderDetail> details = new java.util.ArrayList<>();

    for (Group1.ShoesOnlineShop.entity.Cart cart : cartItems) {
        Product p = cart.getProduct();
        OrderDetail detail = new OrderDetail();
        detail.setOrder(order);
        detail.setProduct(p);
        detail.setQuantity(cart.getQuantity());
        detail.setUnitPrice(p.getPrice());

        BigDecimal sub = p.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
        detail.setSubtotal(sub);
        totalAmount = totalAmount.add(sub);
        
        details.add(detail);
        
        // update product stock quantity
        p.setStockQuantity(p.getStockQuantity() - cart.getQuantity());
        productRepository.save(p);
    }

    order.setTotalAmount(totalAmount);
    order.setOrderDetails(details);

    // Initial Payment Record
    Group1.ShoesOnlineShop.entity.Payment defaultPayment = new Group1.ShoesOnlineShop.entity.Payment();
    defaultPayment.setOrder(order);
    defaultPayment.setPaymentAmount(totalAmount);
    defaultPayment.setPaymentMethod(paymentMethod);
    defaultPayment.setPaymentStatus("PENDING");
    order.setPayments(java.util.Collections.singletonList(defaultPayment));

    return orderRepository.save(order);
}

}
