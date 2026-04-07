package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Delivery;
import Group1.ShoesOnlineShop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrder(Order order);

    @Query("SELECT d FROM Delivery d LEFT JOIN d.shipper s WHERE " +
           "(:keyword IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.order.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR d.deliveryStatus = :status)")
    org.springframework.data.domain.Page<Delivery> searchDeliveries(
            @Param("status") String status,
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT d FROM Delivery d WHERE d.shipper.userId = :shipperId " +
           "AND (:keyword IS NULL OR LOWER(d.order.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR d.deliveryStatus = :status)")
    org.springframework.data.domain.Page<Delivery> searchDeliveriesByShipper(
            @Param("shipperId") Long shipperId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT d FROM Delivery d WHERE d.shipper IS NULL AND d.deliveryStatus = 'PENDING' " +
           "AND (:keyword IS NULL OR LOWER(d.order.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.order.shippingAddress) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    org.springframework.data.domain.Page<Delivery> searchAvailableDeliveries(
            @Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);
}
