package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.MarketingPlan;
import Group1.ShoesOnlineShop.service.MarketingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/internal/plans")
public class MarketingPlanController {

    @Autowired
    private MarketingPlanService marketingPlanService;

    @GetMapping
    public String listPlans(
            Model model,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        Page<MarketingPlan> pagePlans = marketingPlanService.searchPlans(keyword, status, page, size);
        model.addAttribute("plans", pagePlans.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pagePlans.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("size", size);
        
        // Add unread notification count
        long unreadCount = marketingPlanService.countUnreadPlans();
        model.addAttribute("unreadCount", unreadCount);
        
        return "marketing/plan-list";
    }

    @GetMapping("/detail/{id}")
    public String showPlanDetail(@PathVariable(name = "id") Long id, Model model) {
        MarketingPlan plan = marketingPlanService.getPlanById(id);
        if (plan == null) return "redirect:/internal/plans";
        
        // Mark as read if it was unread
        if (plan.getIsRead() != null && !plan.getIsRead()) {
            plan.setIsRead(true);
            marketingPlanService.savePlan(plan);
        }
        
        model.addAttribute("plan", plan);
        return "marketing/plan-detail";
    }

    // Marketing Staff updates plan status (OPEN → IN_PROGRESS → COMPLETED/CANCELLED)
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String newStatus,
                               RedirectAttributes redirectAttributes) {
        MarketingPlan plan = marketingPlanService.getPlanById(id);
        if (plan != null) {
            // Only allow valid transitions
            String current = plan.getStatus();
            boolean valid = false;
            if ("OPEN".equals(current) && "IN_PROGRESS".equals(newStatus)) valid = true;
            if ("IN_PROGRESS".equals(current) && ("COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus))) valid = true;

            if (valid) {
                marketingPlanService.updateStatus(id, newStatus);
                redirectAttributes.addFlashAttribute("successMessage", "Status updated to " + newStatus + "!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid status transition.");
            }
        }
        return "redirect:/internal/plans/detail/" + id;
    }


}
