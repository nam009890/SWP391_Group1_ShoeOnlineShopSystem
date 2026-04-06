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

import java.time.LocalDate;
import java.util.Set;

@Controller
@RequestMapping("/internal/admin")
public class AdminMarketingController {

    private static final Set<String> VALID_TYPES = Set.of("sliders", "coupons", "contents");
    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "APPROVED", "REJECTED", "REMAKE");
    private static final Set<String> VALID_ACTIONS = Set.of("approve", "reject", "remake");

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private SliderRepository sliderRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private MarketingPlanService marketingPlanService;



    // ==================== APPROVAL LISTS (Coupon/Slider/Content) ====================
    @GetMapping("/approvals/{type}")
    public String approvalList(@PathVariable String type, 
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "") String keyword,
                               @RequestParam(defaultValue = "PENDING") String approvalFilter,
                               Model model) {
        // Validate type
        if (!VALID_TYPES.contains(type)) {
            return "redirect:/internal/admin/home";
        }
        // Validate approvalFilter
        if (!VALID_STATUSES.contains(approvalFilter)) {
            approvalFilter = "PENDING";
        }
        // Validate page
        if (page < 1) page = 1;

        int pageSize = 10;
        String kw = keyword.trim();
        boolean hasKeyword = !kw.isEmpty();

        if ("coupons".equals(type)) {
            Page<Coupon> items = hasKeyword
                ? couponRepository.findByApprovalStatusAndCouponNameContainingIgnoreCase(approvalFilter, kw, PageRequest.of(page - 1, pageSize))
                : couponRepository.findByApprovalStatus(approvalFilter, PageRequest.of(page - 1, pageSize));
            model.addAttribute("items", items);
        } else if ("sliders".equals(type)) {
            Page<Slider> items = hasKeyword
                ? sliderRepository.findByApprovalStatusAndSliderTitleContainingIgnoreCase(approvalFilter, kw, PageRequest.of(page - 1, pageSize))
                : sliderRepository.findByApprovalStatus(approvalFilter, PageRequest.of(page - 1, pageSize));
            model.addAttribute("items", items);
        } else {
            Page<Content> items = hasKeyword
                ? contentRepository.findByApprovalStatusAndContentTitleContainingIgnoreCase(approvalFilter, kw, PageRequest.of(page - 1, pageSize))
                : contentRepository.findByApprovalStatus(approvalFilter, PageRequest.of(page - 1, pageSize));
            model.addAttribute("items", items);
        }
        
        model.addAttribute("type", type);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("approvalFilter", approvalFilter);
        
        if ("coupons".equals(type)) return "admin/admin-coupon-approval-list";
        if ("contents".equals(type)) return "admin/admin-content-approval-list";
        return "admin/admin-slider-approval-list";
    }

    // ==================== APPROVAL DETAIL (Coupon/Slider/Content) ====================
    @GetMapping("/approvals/{type}/detail/{id}")
    public String approvalDetail(@PathVariable String type, @PathVariable Long id, Model model) {
        if (!VALID_TYPES.contains(type)) {
            return "redirect:/internal/admin/home";
        }
        if (id == null || id <= 0) {
            return "redirect:/internal/admin/approvals/" + type;
        }

        if ("coupons".equals(type)) {
            Coupon item = couponRepository.findById(id).orElse(null);
            if (item == null) return "redirect:/internal/admin/approvals/coupons";
            model.addAttribute("item", item);
        } else if ("sliders".equals(type)) {
            Slider item = sliderRepository.findById(id).orElse(null);
            if (item == null) return "redirect:/internal/admin/approvals/sliders";
            model.addAttribute("item", item);
        } else {
            Content item = contentRepository.findById(id).orElse(null);
            if (item == null) return "redirect:/internal/admin/approvals/contents";
            model.addAttribute("item", item);
        }
        model.addAttribute("type", type);
        
        if ("coupons".equals(type)) return "admin/admin-coupon-approval-detail";
        if ("contents".equals(type)) return "admin/admin-content-approval-detail";
        return "admin/admin-slider-approval-detail";
    }

    // ==================== PROCESS APPROVAL ====================
    @PostMapping("/process-approval")
    public String processApproval(@RequestParam String type,
                                  @RequestParam Long id,
                                  @RequestParam String action,
                                  @RequestParam(required = false) String remakeNote,
                                  RedirectAttributes redirectAttributes) {
        // Validate type
        if (!VALID_TYPES.contains(type)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid type!");
            return "redirect:/internal/admin/home";
        }
        // Validate action
        if (!VALID_ACTIONS.contains(action)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid action!");
            return "redirect:/internal/admin/approvals/" + type;
        }
        // Validate id
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid item ID!");
            return "redirect:/internal/admin/approvals/" + type;
        }
        // Validate remakeNote is required for remake action
        if ("remake".equals(action) && (remakeNote == null || remakeNote.trim().isEmpty())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Remake note is required when requesting a remake!");
            return "redirect:/internal/admin/approvals/" + type + "/detail/" + id;
        }
                                      
        String newStatus = "PENDING";
        if ("approve".equals(action)) newStatus = "APPROVED";
        else if ("reject".equals(action)) newStatus = "REJECTED";
        else if ("remake".equals(action)) newStatus = "REMAKE";
        
        if ("coupons".equals(type)) {
            Coupon c = couponRepository.findById(id).orElse(null);
            if (c == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Coupon not found!");
                return "redirect:/internal/admin/approvals/coupons";
            }
            if (!"PENDING".equals(c.getApprovalStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only PENDING items can be processed!");
                return "redirect:/internal/admin/approvals/coupons";
            }
            if ("approve".equals(action) && "DELETE_REQUEST".equals(c.getRemakeNote())) {
                couponRepository.delete(c);
            } else {
                c.setApprovalStatus(newStatus);
                c.setRemakeNote("remake".equals(action) ? remakeNote.trim() : null);
                couponRepository.save(c);
            }
        } else if ("sliders".equals(type)) {
            Slider s = sliderRepository.findById(id).orElse(null);
            if (s == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Slider not found!");
                return "redirect:/internal/admin/approvals/sliders";
            }
            if (!"PENDING".equals(s.getApprovalStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only PENDING items can be processed!");
                return "redirect:/internal/admin/approvals/sliders";
            }
            if ("approve".equals(action) && "DELETE_REQUEST".equals(s.getRemakeNote())) {
                sliderRepository.delete(s);
            } else {
                s.setApprovalStatus(newStatus);
                s.setRemakeNote("remake".equals(action) ? remakeNote.trim() : null);
                sliderRepository.save(s);
            }
        } else {
            Content c = contentRepository.findById(id).orElse(null);
            if (c == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Content not found!");
                return "redirect:/internal/admin/approvals/contents";
            }
            if (!"PENDING".equals(c.getApprovalStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Only PENDING items can be processed!");
                return "redirect:/internal/admin/approvals/contents";
            }
            if ("approve".equals(action) && "DELETE_REQUEST".equals(c.getRemakeNote())) {
                contentRepository.delete(c);
            } else {
                c.setApprovalStatus(newStatus);
                c.setRemakeNote("remake".equals(action) ? remakeNote.trim() : null);
                contentRepository.save(c);
            }
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "Item " + action + "d successfully!");
        return "redirect:/internal/admin/approvals/" + type;
    }

    // ==================== PROCESS BATCH APPROVAL ====================
    @PostMapping("/process-batch-approval")
    public String processBatchApproval(@RequestParam String type,
                                       @RequestParam(required = false) java.util.List<Long> itemIds,
                                       @RequestParam String action,
                                       RedirectAttributes redirectAttributes) {
        // Validate type
        if (!VALID_TYPES.contains(type)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid type!");
            return "redirect:/internal/admin/home";
        }
        // Validate action (only approve/reject allowed for batch)
        if (!"approve".equals(action) && !"reject".equals(action)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid batch action! Only approve or reject allowed.");
            return "redirect:/internal/admin/approvals/" + type;
        }
        // Validate items selected
        if (itemIds == null || itemIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No items selected! Please check at least one item.");
            return "redirect:/internal/admin/approvals/" + type;
        }

        String newStatus = "approve".equals(action) ? "APPROVED" : "REJECTED";
        int count = 0;
        int skipped = 0;

        for (Long id : itemIds) {
            if (id == null || id <= 0) {
                skipped++;
                continue;
            }
            if ("coupons".equals(type)) {
                Coupon c = couponRepository.findById(id).orElse(null);
                if (c != null && "PENDING".equals(c.getApprovalStatus())) {
                    if ("approve".equals(action) && "DELETE_REQUEST".equals(c.getRemakeNote())) {
                        couponRepository.delete(c);
                    } else {
                        c.setApprovalStatus(newStatus);
                        c.setRemakeNote(null);
                        couponRepository.save(c);
                    }
                    count++;
                } else {
                    skipped++;
                }
            } else if ("sliders".equals(type)) {
                Slider s = sliderRepository.findById(id).orElse(null);
                if (s != null && "PENDING".equals(s.getApprovalStatus())) {
                    if ("approve".equals(action) && "DELETE_REQUEST".equals(s.getRemakeNote())) {
                        sliderRepository.delete(s);
                    } else {
                        s.setApprovalStatus(newStatus);
                        s.setRemakeNote(null);
                        sliderRepository.save(s);
                    }
                    count++;
                } else {
                    skipped++;
                }
            } else {
                Content c = contentRepository.findById(id).orElse(null);
                if (c != null && "PENDING".equals(c.getApprovalStatus())) {
                    if ("approve".equals(action) && "DELETE_REQUEST".equals(c.getRemakeNote())) {
                        contentRepository.delete(c);
                    } else {
                        c.setApprovalStatus(newStatus);
                        c.setRemakeNote(null);
                        contentRepository.save(c);
                    }
                    count++;
                } else {
                    skipped++;
                }
            }
        }

        if (count == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "No eligible items were processed. Items must be in PENDING status.");
        } else {
            String msg = count + " item(s) " + action + "d successfully!";
            if (skipped > 0) {
                msg += " (" + skipped + " item(s) skipped - not PENDING or not found)";
            }
            redirectAttributes.addFlashAttribute("successMessage", msg);
        }
        return "redirect:/internal/admin/approvals/" + type;
    }

    // ==================== MARKETING PLANS ====================
    @GetMapping("/marketing-plans")
    public String listPlans(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(required = false) String approvalStatus,
                            @RequestParam(required = false) String status,
                            Model model) {
        Page<MarketingPlan> plans = marketingPlanService.searchPlansForManager(keyword, approvalStatus, status, page, 10);
        model.addAttribute("plans", plans);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("approvalStatus", approvalStatus);
        model.addAttribute("status", status);
        return "admin/admin-plan-list";
    }

    @GetMapping("/marketing-plans/detail/{id}")
    public String planDetail(@PathVariable Long id, Model model) {
        MarketingPlan plan = marketingPlanService.getPlanById(id);
        if (plan == null) return "redirect:/internal/admin/marketing-plans";
        model.addAttribute("plan", plan);
        return "admin/admin-plan-detail";
    }

    @GetMapping("/marketing-plans/create")
    public String showPlanForm(Model model) {
        model.addAttribute("plan", new MarketingPlan());
        return "admin/admin-plan-form";
    }

    @GetMapping("/marketing-plans/edit/{id}")
    public String showEditPlanForm(@PathVariable Long id, Model model) {
        MarketingPlan plan = marketingPlanService.getPlanById(id);
        if (plan == null) return "redirect:/internal/admin/marketing-plans";
        model.addAttribute("plan", plan);
        return "admin/admin-plan-form";
    }

    @PostMapping("/marketing-plans/save")
    public String savePlan(@ModelAttribute("plan") MarketingPlan plan, RedirectAttributes redirectAttributes) {
        // Validate dates
        if (plan.getStartDate() != null && plan.getEndDate() != null
                && plan.getEndDate().isBefore(plan.getStartDate())) {
            redirectAttributes.addFlashAttribute("errorMessage", "End date must be after or equal to start date!");
            if (plan.getId() != null) {
                return "redirect:/internal/admin/marketing-plans/edit/" + plan.getId();
            }
            return "redirect:/internal/admin/marketing-plans/create";
        }
        marketingPlanService.savePlan(plan);
        redirectAttributes.addFlashAttribute("successMessage", "Marketing Plan saved!");
        return "redirect:/internal/admin/marketing-plans";
    }

    @PostMapping("/marketing-plans/delete/{id}")
    public String deletePlan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        MarketingPlan plan = marketingPlanService.getPlanById(id);
        if (plan == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Plan not found!");
        } else {
            marketingPlanService.deletePlan(id);
            redirectAttributes.addFlashAttribute("successMessage", "Plan deleted successfully!");
        }
        return "redirect:/internal/admin/marketing-plans";
    }

    // Reopen an overdue plan
    @PostMapping("/marketing-plans/{id}/reopen")
    public String reopenPlan(@PathVariable Long id,
                             @RequestParam(required = false) String newEndDate,
                             RedirectAttributes redirectAttributes) {
        LocalDate endDate = null;
        if (newEndDate != null && !newEndDate.isEmpty()) {
            endDate = LocalDate.parse(newEndDate);
        }
        marketingPlanService.reopenPlan(id, endDate);
        redirectAttributes.addFlashAttribute("successMessage", "Plan reopened successfully!");
        return "redirect:/internal/admin/marketing-plans";
    }
}
