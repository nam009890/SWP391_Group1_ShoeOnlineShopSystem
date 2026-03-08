/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.repository;
import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.entity.OrderStatus;
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
    
}
