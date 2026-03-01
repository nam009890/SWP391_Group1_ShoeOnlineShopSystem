package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Feedback;
import Group1.ShoesOnlineShop.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Page<Feedback> getAll(String status, String keyword, int page) {

        Pageable pageable = PageRequest.of(page, 5, Sort.by("feedbackId").descending());

        Boolean approved = null;

        if (status != null && !status.isEmpty()) {
            approved = status.equals("Active");
        }

        if (approved != null && keyword != null && !keyword.isEmpty()) {
            return feedbackRepository
                    .findByIsApprovedAndUser_FullNameContainingIgnoreCase(
                            approved, keyword, pageable);
        }

        if (approved != null) {
            return feedbackRepository.findByIsApproved(approved, pageable);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return feedbackRepository
                    .findByUser_FullNameContainingIgnoreCase(keyword, pageable);
        }

        return feedbackRepository.findAll(pageable);
    }

    public void toggleStatus(Long id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow();
        feedback.setIsApproved(!feedback.getIsApproved());
        feedbackRepository.save(feedback);
    }

    public void delete(Long id) {
        feedbackRepository.deleteById(id);
    }
    
    public Feedback getById(Long id) {
    return feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
}
}