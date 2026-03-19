package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/feedbacks")
public class StaffFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String listFeedbacks(Model model) {
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "staff-feedback-list";
    }

    @PostMapping("/approve/{id}")
    public String approveFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedbackService.approveFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback approved successfully.");
        return "redirect:/staff/feedbacks";
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedbackService.deleteFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted successfully.");
        return "redirect:/staff/feedbacks";
    }
}
