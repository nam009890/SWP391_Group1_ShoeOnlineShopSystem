/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.repository;
import Group1.ShoesOnlineShop.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/**
 *
 * @author Windows
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT o FROM Order o
        WHERE o.isActive = true
        AND (:status IS NULL OR o.orderStatus = :status)
        AND (:keyword IS NULL OR LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Order> searchOrders(@Param("status") String status,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    List<Order> findByOrderStatus(String status);
    List<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
        SELECT o FROM Order o
        JOIN FETCH o.user
        LEFT JOIN FETCH o.orderDetails od
        LEFT JOIN FETCH od.product
        LEFT JOIN FETCH o.coupon
        WHERE o.orderId = :id
    """)
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    @Query("""
        SELECT COUNT(o) > 0 FROM Order o 
        JOIN o.orderDetails od 
        WHERE o.user.userId = :userId 
        AND od.product.id = :productId 
        AND (o.orderStatus = 'DELIVERED' OR o.paymentStatus = 'PAID')
    """)
     boolean hasPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
