package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.MarketingPlan;
import Group1.ShoesOnlineShop.repository.MarketingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MarketingPlanService {

    @Autowired
    private MarketingPlanRepository marketingPlanRepository;

    public Page<MarketingPlan> getPlans(int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
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
}
