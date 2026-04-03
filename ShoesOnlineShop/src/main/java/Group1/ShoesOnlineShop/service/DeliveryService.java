package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Delivery;
import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.DeliveryRepository;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           OrderRepository orderRepository,
                           UserRepository userRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Delivery assignShipperToOrder(Long orderId, Long shipperId, BigDecimal shippingFee, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        User shipper = userRepository.findById(shipperId)
                .orElseThrow(() -> new IllegalArgumentException("Shipper not found"));

        if (!"SHIPPER".equalsIgnoreCase(shipper.getUserRole())) {
            throw new IllegalArgumentException("Selected user is not a shipper");
        }

        Delivery delivery = deliveryRepository.findByOrder(order).orElse(new Delivery());
        delivery.setOrder(order);
        delivery.setShipper(shipper);
        delivery.setShippingFee(shippingFee != null ? shippingFee : BigDecimal.ZERO);
        delivery.setNote(note);
        delivery.setAssignedAt(LocalDateTime.now());
        delivery.setDeliveryStatus("PENDING");
        
        if (delivery.getTrackingNumber() == null) {
            delivery.setTrackingNumber("TRK" + System.currentTimeMillis() + java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        }
        
        return deliveryRepository.save(delivery);
    }

    public org.springframework.data.domain.Page<Delivery> getDeliveries(String status, String keyword, int page, String sort) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, 5, org.springframework.data.domain.Sort.by(sort));
        if (status != null && status.isEmpty()) status = null;
        if (keyword != null && keyword.isEmpty()) keyword = null;
        return deliveryRepository.searchDeliveries(status, keyword, pageable);
    }

    public Delivery findById(Long id) {
        return deliveryRepository.findById(id).orElseThrow(() -> new RuntimeException("Delivery not found"));
    }

    @Transactional
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(() -> new RuntimeException("Delivery not found"));
        String st = delivery.getDeliveryStatus();
        if (!"PENDING".equals(st) && !"DELIVERED".equals(st) && !"FAILED".equals(st)) {
            throw new RuntimeException("Cannot delete delivery when status is " + st);
        }
        deliveryRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setDeliveryStatus(status);
        if ("DELIVERED".equals(status) && delivery.getDeliveredDate() == null) {
            delivery.setDeliveredDate(LocalDateTime.now());
        }
        if ("PICKED_UP".equals(status) && delivery.getShippedDate() == null) {
            delivery.setShippedDate(LocalDateTime.now());
        }
        deliveryRepository.save(delivery);
    }
}
