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
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;
import org.springframework.util.StringUtils;

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
        
        User shipper = null;
        if (shipperId != null) {
            shipper = userRepository.findById(shipperId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipper not found"));
            if (!"SHIPPER".equalsIgnoreCase(shipper.getUserRole())) {
                throw new IllegalArgumentException("Selected user is not a shipper");
            }
        }

        Delivery delivery = deliveryRepository.findByOrder(order).orElse(new Delivery());
        delivery.setOrder(order);
        delivery.setShipper(shipper);
        delivery.setShippingFee(shippingFee != null ? shippingFee : BigDecimal.ZERO);
        delivery.setNote(note);
        if (shipper != null) {
            delivery.setAssignedAt(LocalDateTime.now());
            delivery.setDeliveryStatus("ASSIGNED");
        } else {
            delivery.setDeliveryStatus("PENDING");
        }
        
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

    public org.springframework.data.domain.Page<Delivery> getDeliveriesByShipper(Long shipperId, String status, String keyword, int page, String sort) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, 5, org.springframework.data.domain.Sort.by(sort));
        if (status != null && status.isEmpty()) status = null;
        if (keyword != null && keyword.isEmpty()) keyword = null;
        return deliveryRepository.searchDeliveriesByShipper(shipperId, status, keyword, pageable);
    }

    public org.springframework.data.domain.Page<Delivery> getAvailableDeliveries(String keyword, int page, String sort) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, 5, org.springframework.data.domain.Sort.by(sort));
        if (keyword != null && keyword.isEmpty()) keyword = null;
        return deliveryRepository.searchAvailableDeliveries(keyword, pageable);
    }

    @Transactional
    public void acceptDelivery(Long deliveryId, Long shipperId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        if (delivery.getShipper() != null) {
            throw new IllegalArgumentException("Delivery has already been assigned to a shipper");
        }
        User shipper = userRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));
        delivery.setShipper(shipper);
        delivery.setDeliveryStatus("ASSIGNED");
        delivery.setAssignedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
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

    @Transactional
    public void updateStatusWithImage(Long id, String status, MultipartFile imageFile) throws java.io.IOException {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setDeliveryStatus(status);
        if ("DELIVERED".equals(status)) {
            if (delivery.getDeliveredDate() == null) {
                delivery.setDeliveredDate(LocalDateTime.now());
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path uploadPath = Group1.ShoesOnlineShop.config.WebMvcConfig.UPLOAD_DIR;
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    delivery.setProofImageUrl("/uploads/" + fileName);
                }
            } else if (delivery.getProofImageUrl() == null) {
                throw new IllegalArgumentException("Proof image is required to mark as DELIVERED");
            }
        }
        if ("PICKED_UP".equals(status) && delivery.getShippedDate() == null) {
            delivery.setShippedDate(LocalDateTime.now());
        }
        deliveryRepository.save(delivery);
    }
}
