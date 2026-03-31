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
}
