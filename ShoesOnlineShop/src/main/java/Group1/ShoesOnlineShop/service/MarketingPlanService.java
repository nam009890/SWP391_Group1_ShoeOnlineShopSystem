package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.MarketingPlan;
import Group1.ShoesOnlineShop.repository.MarketingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Service
public class MarketingPlanService {

    @Autowired
    private MarketingPlanRepository marketingPlanRepository;

    public Page<MarketingPlan> getPlans(int page, int size) {

        Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        return marketingPlanRepository.findAll(paging);
    }
    
    public Page<MarketingPlan> getPlansByRole(String role, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        return marketingPlanRepository.findByAssignedRole(role, paging);
    }

    public MarketingPlan getPlanById(Long id) {
        return marketingPlanRepository.findById(id).orElse(null);
    }

    public void savePlan(MarketingPlan plan) {
        if (plan.getId() == null) {
            plan.setCreatedAt(LocalDateTime.now());
        }
        plan.setUpdatedAt(LocalDateTime.now());
        marketingPlanRepository.save(plan);
    }

    public void deletePlan(Long id) {
        marketingPlanRepository.deleteById(id);
    }
    
    public void updateStatus(Long id, String status) {
        MarketingPlan plan = getPlanById(id);
        if (plan != null) {
            plan.setStatus(status);
            plan.setUpdatedAt(LocalDateTime.now());
            marketingPlanRepository.save(plan);
        }
    }


    /**
     * Search plans with optional keyword and status filter.
     */
    public Page<MarketingPlan> searchPlans(String keyword, String status, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        
        String kw = keyword == null ? "" : keyword.trim();
        boolean hasKeyword = !kw.isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasKeyword && hasStatus) {
            return marketingPlanRepository.findByTitleContainingIgnoreCaseAndStatus(kw, status, paging);
        } else if (hasKeyword) {
            return marketingPlanRepository.findByTitleContainingIgnoreCase(kw, paging);
        } else if (hasStatus) {
            return marketingPlanRepository.findByStatus(status, paging);
        } else {
            return marketingPlanRepository.findAll(paging);
        }
    }

    /**
     * Get approved plans for homepage display (only OPEN or IN_PROGRESS).
     */
    public List<MarketingPlan> getApprovedActivePlans() {
        return marketingPlanRepository.findByApprovalStatusAndStatusIn(
            "APPROVED", Arrays.asList("OPEN", "IN_PROGRESS")
        );
    }

    /**
     * Get plans pending approval (for Admin).
     */
    public Page<MarketingPlan> getPendingApprovalPlans(int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return marketingPlanRepository.findByApprovalStatus("PENDING", paging);
    }

    /**
     * Count pending approvals.
     */
    public long countPendingApprovals() {
        return marketingPlanRepository.countByApprovalStatus("PENDING");
    }

    /**
     * Reopen a plan that is overdue — reset status to OPEN and extend endDate.
     */
    public void reopenPlan(Long id, java.time.LocalDate newEndDate) {
        MarketingPlan plan = getPlanById(id);
        if (plan != null) {
            plan.setStatus("OPEN");
            if (newEndDate != null) {
                plan.setEndDate(newEndDate);
            }
            plan.setUpdatedAt(LocalDateTime.now());
            marketingPlanRepository.save(plan);
        }
    }

    /**
     * Search plans for Admin with keyword + approval status filter.
     */
    public Page<MarketingPlan> searchPlansForManager(String keyword, String approvalStatus, String status, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        String kw = keyword == null ? "" : keyword.trim();
        boolean hasKw = !kw.isEmpty();
        boolean hasApproval = approvalStatus != null && !approvalStatus.trim().isEmpty();
        boolean hasSt = status != null && !status.trim().isEmpty();

        if (hasKw && hasApproval) {
            return marketingPlanRepository.findByTitleContainingIgnoreCaseAndApprovalStatus(kw, approvalStatus, paging);
        } else if (hasKw && hasSt) {
            return marketingPlanRepository.findByTitleContainingIgnoreCaseAndStatus(kw, status, paging);
        } else if (hasKw) {
            return marketingPlanRepository.findByTitleContainingIgnoreCase(kw, paging);
        } else if (hasApproval) {
            return marketingPlanRepository.findByApprovalStatus(approvalStatus, paging);
        } else if (hasSt) {
            return marketingPlanRepository.findByStatus(status, paging);
        } else {
            return marketingPlanRepository.findAll(paging);
        }
    }

    /**
     * Count unread plans (for Marketing Staff notification).
     */
    public long countUnreadPlans() {
        return marketingPlanRepository.countByIsReadFalse();
    }
}


