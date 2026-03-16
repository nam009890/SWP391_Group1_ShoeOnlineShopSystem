package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProduct_IdOrderByCreatedAtDesc(Long productId);
    boolean existsByOrderOrderIdAndProduct_Id(Long orderId, Long productId);
}
