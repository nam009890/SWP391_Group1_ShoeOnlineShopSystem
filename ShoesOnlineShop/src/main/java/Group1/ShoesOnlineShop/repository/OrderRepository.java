package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    List<Order> findByUserUserIdOrderByOrderDateDesc(Long userId);
}
