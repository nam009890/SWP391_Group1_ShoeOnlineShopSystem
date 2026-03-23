package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrder_OrderIdOrderByTimestampAsc(Long orderId);
}
