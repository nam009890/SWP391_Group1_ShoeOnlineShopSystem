package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.entity.OrderDetail;
import Group1.ShoesOnlineShop.entity.Cart;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import Group1.ShoesOnlineShop.repository.OrderDetailRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomerOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    public Page<Order> getOrdersByUser(Long userId, int page, int size) {
        return orderRepository.findByUserUserIdOrderByOrderDateDesc(userId, PageRequest.of(page - 1, size));
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Transactional
    public Order placeOrder(Long userId, String sessionId, String shippingAddress, String phone) {
        List<Cart> cartItems = cartService.getCartItems(userId, sessionId);
        if (cartItems.isEmpty()) return null;

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null; // Guest checkout not fully supported in this simplified version

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPhone(phone);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        
        Order savedOrder = orderRepository.save(order);

        for (Cart item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getProduct().getPrice());
            detail.setSubtotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderDetailRepository.save(detail);

            // Optional: Reduce product stock here
        }

        cartService.clearCart(userId, sessionId);
        return savedOrder;
    }
}
