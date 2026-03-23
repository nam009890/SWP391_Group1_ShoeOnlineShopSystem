package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import Group1.ShoesOnlineShop.entity.Feedback;
@Controller
@RequestMapping("/internal/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "feedbackId") String sort,
            Model model) {

        Page<?> feedbacks = feedbackService.getAll(status, keyword, page, sort);

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        return "feedback-list";
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam Long id) {
        feedbackService.toggleStatus(id);
        return "redirect:/internal/feedbacks";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return "redirect:/internal/feedbacks";
    }
    
    @GetMapping("/view/{id}")
public String viewDetail(@PathVariable Long id, Model model) {

    Feedback feedback = feedbackService.getById(id);

    model.addAttribute("feedback", feedback);

    return "feedback-detail";
}
}