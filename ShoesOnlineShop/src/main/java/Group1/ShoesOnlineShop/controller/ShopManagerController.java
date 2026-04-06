package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.entity.MarketingPlan;
import Group1.ShoesOnlineShop.service.MarketingPlanService;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.SliderRepository;
import Group1.ShoesOnlineShop.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/internal/shop-manager")
public class ShopManagerController {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private SliderRepository sliderRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private MarketingPlanService marketingPlanService;

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Pending counts
        long pendingCoupons = couponRepository.findByApprovalStatus("PENDING", PageRequest.of(0, 1)).getTotalElements();
        long pendingSliders = sliderRepository.findByApprovalStatus("PENDING", PageRequest.of(0, 1)).getTotalElements();
        long pendingContents = contentRepository.findByApprovalStatus("PENDING", PageRequest.of(0, 1)).getTotalElements();
        
        model.addAttribute("pendingCoupons", pendingCoupons);
        model.addAttribute("pendingSliders", pendingSliders);
        model.addAttribute("pendingContents", pendingContents);
        
        return "shop-manager/dashboard";
    }

    // --- APPROVAL LISTS ---
    @GetMapping("/approvals/{type}")
    public String approvalList(@PathVariable String type, 
                               @RequestParam(defaultValue = "1") int page,
                               Model model) {
        int pageSize = 10;
        if ("coupons".equals(type)) {
            Page<Coupon> items = couponRepository.findByApprovalStatus("PENDING", PageRequest.of(page - 1, pageSize));
            model.addAttribute("items", items);
        } else if ("sliders".equals(type)) {
            Page<Slider> items = sliderRepository.findByApprovalStatus("PENDING", PageRequest.of(page - 1, pageSize));
            model.addAttribute("items", items);
        } else if ("contents".equals(type)) {
            Page<Content> items = contentRepository.findByApprovalStatus("PENDING", PageRequest.of(page - 1, pageSize));
            model.addAttribute("items", items);
        } else {
            return "redirect:/internal/shop-manager/dashboard";
        }
        
        model.addAttribute("type", type);
        model.addAttribute("currentPage", page);
        return "shop-manager/approval-list";
    }

    // --- PROCESS APPROVAL ---
    @PostMapping("/process-approval")
    public String processApproval(@RequestParam String type,
                                  @RequestParam Long id,
                                  @RequestParam String action,
                                  @RequestParam(required = false) String remakeNote,
                                  RedirectAttributes redirectAttributes) {
                                      
        String newStatus = "PENDING";
        if ("approve".equals(action)) newStatus = "APPROVED";
        else if ("reject".equals(action)) newStatus = "REJECTED";
        else if ("remake".equals(action)) newStatus = "REMAKE";
        
        if ("coupons".equals(type)) {
            Coupon c = couponRepository.findById(id).orElse(null);
            if (c != null) {
                c.setApprovalStatus(newStatus);
                c.setRemakeNote(remakeNote);
                couponRepository.save(c);
            }
        } else if ("sliders".equals(type)) {
            Slider s = sliderRepository.findById(id).orElse(null);
            if (s != null) {
                s.setApprovalStatus(newStatus);
                s.setRemakeNote(remakeNote);
                sliderRepository.save(s);
            }
        } else if ("contents".equals(type)) {
            Content c = contentRepository.findById(id).orElse(null);
            if (c != null) {
                c.setApprovalStatus(newStatus);
                c.setRemakeNote(remakeNote);
                contentRepository.save(c);
            }
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Item " + action + " successfully!");
        return "redirect:/internal/shop-manager/approvals/" + type;
    }

    // --- MARKETING PLANS ---
    @GetMapping("/marketing-plans")
    public String listPlans(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<MarketingPlan> plans = marketingPlanService.getPlans(page, 10);
        model.addAttribute("plans", plans);
        model.addAttribute("currentPage", page);
        return "shop-manager/plan-list";
    }

    @GetMapping("/marketing-plans/create")
    public String showPlanForm(Model model) {
        model.addAttribute("plan", new MarketingPlan());
        return "shop-manager/plan-form";
    }

    @PostMapping("/marketing-plans/save")
    public String savePlan(@ModelAttribute("plan") MarketingPlan plan, RedirectAttributes redirectAttributes) {
        marketingPlanService.savePlan(plan);
        redirectAttributes.addFlashAttribute("successMessage", "Marketing Plan saved!");
        return "redirect:/internal/shop-manager/marketing-plans";
    }
}
