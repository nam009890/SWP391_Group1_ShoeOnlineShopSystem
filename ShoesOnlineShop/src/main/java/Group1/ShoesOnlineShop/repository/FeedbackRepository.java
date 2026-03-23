package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Page<Feedback> findByIsApproved(Boolean isApproved, Pageable pageable);

    Page<Feedback> findByUser_FullNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Feedback> findByIsApprovedAndUser_FullNameContainingIgnoreCase(
            Boolean isApproved,
            String keyword,
            Pageable pageable
    );

    // Approved feedbacks for a product (customer-facing)
    java.util.List<Feedback> findByProduct_IdAndIsApprovedTrueOrderByCreatedAtDesc(Long productId);
}