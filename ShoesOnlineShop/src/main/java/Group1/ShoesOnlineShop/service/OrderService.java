/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.service;
import Group1.ShoesOnlineShop.entity.Coupon;
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
    
    @Autowired
    private Group1.ShoesOnlineShop.repository.OrderHistoryRepository orderHistoryRepository;
    
    @Autowired
    private CartService cartService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private void saveOrderHistory(Order order, String status, String note) {
        Group1.ShoesOnlineShop.entity.OrderHistory history = new Group1.ShoesOnlineShop.entity.OrderHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setNote(note);
        history.setTimestamp(java.time.LocalDateTime.now());
        orderHistoryRepository.save(history);
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
    
    saveOrderHistory(order, status, "Order status updated to " + status);
}

    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }
        
        if (!"PENDING".equalsIgnoreCase(order.getOrderStatus())) {
            throw new RuntimeException("Only PENDING orders can be cancelled");
        }
        
        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);
        saveOrderHistory(order, "CANCELLED", "Order cancelled by customer");
    }

    public void reorder(Long userId, Long orderId, String sessionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        User user = order.getUser();
        for (OrderDetail detail : order.getOrderDetails()) {
            cartService.addToCart(
                detail.getProduct().getId(), 
                detail.getQuantity(), 
                detail.getProduct().getSize(), // Use original size if stored in detail, but here it's likely fixed or we use product's default
                detail.getProduct().getColor(), // Using product properties as fallback if detail doesn't house variant info
                user.getUserName(), 
                sessionId
            );
        }
    }

    public List<Group1.ShoesOnlineShop.entity.OrderHistory> getTimeline(Long orderId) {
        return orderHistoryRepository.findByOrder_OrderIdOrderByTimestampAsc(orderId);
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
public Order createOrderFromCart(User user, java.util.List<Group1.ShoesOnlineShop.entity.Cart> cartItems, String phone, String address, String paymentMethod, Coupon coupon) {
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
    
    // Apply Coupon Discount
    if (coupon != null) {
        if (coupon.getMinOrderValue() != null && totalAmount.compareTo(BigDecimal.valueOf(coupon.getMinOrderValue())) < 0) {
            throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu " + coupon.getMinOrderValue() + "đ để áp dụng mã này");
        }
        
        order.setCoupon(coupon);
        
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal eligibleAmount = BigDecimal.ZERO;
        
        boolean isSpecific = "SPECIFIC_PRODUCTS".equals(coupon.getScope());
        java.util.List<Long> eligibleProductIds = new java.util.ArrayList<>();
        if (isSpecific && coupon.getProducts() != null) {
            for (Product p : coupon.getProducts()) {
                eligibleProductIds.add(p.getProductId());
            }
        }
        
        for (Group1.ShoesOnlineShop.entity.Cart cart : cartItems) {
            Product p = cart.getProduct();
            // Ràng buộc User yêu cầu: coupon chỉ áp dụng cho sản phẩm CÒN HÀNG (tồn kho ban đầu > 0, đây là tồn kho hiện tại)
            if (p.getStockQuantity() >= 0) { // Đã bị trừ ở vòng lặp trên nên có thể = 0. Nếu muốn chặt hơn thì check tồn kho ban đầu = p.getStockQuantity() + cart.getQuantity() > 0
                if (!isSpecific || eligibleProductIds.contains(p.getProductId())) {
                    eligibleAmount = eligibleAmount.add(p.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
                }
            }
        }

        if (eligibleAmount.compareTo(BigDecimal.ZERO) > 0) {
            if ("PERCENTAGE".equals(coupon.getDiscountType())) {
                discountAmount = eligibleAmount.multiply(BigDecimal.valueOf(coupon.getDiscountValue())).divide(BigDecimal.valueOf(100));
                if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(BigDecimal.valueOf(coupon.getMaxDiscountAmount())) > 0) {
                    discountAmount = BigDecimal.valueOf(coupon.getMaxDiscountAmount());
                }
            } else if ("FIXED_AMOUNT".equals(coupon.getDiscountType())) {
                discountAmount = BigDecimal.valueOf(coupon.getDiscountValue());
                if (discountAmount.compareTo(eligibleAmount) > 0) {
                    discountAmount = eligibleAmount;
                }
            }
        }
        
        totalAmount = totalAmount.subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) totalAmount = BigDecimal.ZERO;
        
        order.setTotalAmount(totalAmount);
    }
    
    order.setOrderDetails(details);

    // Initial Payment Record
    Group1.ShoesOnlineShop.entity.Payment defaultPayment = new Group1.ShoesOnlineShop.entity.Payment();
    defaultPayment.setOrder(order);
    defaultPayment.setPaymentAmount(totalAmount);
    defaultPayment.setPaymentMethod(paymentMethod);
    defaultPayment.setPaymentStatus("PENDING");
    order.setPayments(java.util.Collections.singletonList(defaultPayment));

    Order savedOrder = orderRepository.save(order);
    saveOrderHistory(savedOrder, "PENDING", "Order placed successfully");
    return savedOrder;
}

}
