package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<Feedback> findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(Long productId);
    List<Feedback> findAllByOrderByCreatedAtDesc();
    boolean existsByOrderOrderIdAndProductId(Long orderId, Long productId);
}
