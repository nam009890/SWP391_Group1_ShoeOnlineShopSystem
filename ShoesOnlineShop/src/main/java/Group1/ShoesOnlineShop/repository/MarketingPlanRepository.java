package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.MarketingPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketingPlanRepository extends JpaRepository<MarketingPlan, Long> {
    
    Page<MarketingPlan> findByAssignedRole(String assignedRole, Pageable pageable);
    
    List<MarketingPlan> findByAssignedRoleAndStatus(String assignedRole, String status);
    
    Page<MarketingPlan> findAll(Pageable pageable);

    // Search by title
    Page<MarketingPlan> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Filter by status
    Page<MarketingPlan> findByStatus(String status, Pageable pageable);

    // Search + filter status
    Page<MarketingPlan> findByTitleContainingIgnoreCaseAndStatus(String title, String status, Pageable pageable);

    // Approval queries (for Admin)
    Page<MarketingPlan> findByApprovalStatus(String approvalStatus, Pageable pageable);

    // For homepage: approved plans that are active (OPEN or IN_PROGRESS)
    List<MarketingPlan> findByApprovalStatusAndStatusIn(String approvalStatus, List<String> statuses);

    // Count by approval status
    long countByApprovalStatus(String approvalStatus);

    // Search + filter by approval status
    Page<MarketingPlan> findByTitleContainingIgnoreCaseAndApprovalStatus(String title, String approvalStatus, Pageable pageable);

    // Count unread notifications
    long countByIsReadFalse();
}

