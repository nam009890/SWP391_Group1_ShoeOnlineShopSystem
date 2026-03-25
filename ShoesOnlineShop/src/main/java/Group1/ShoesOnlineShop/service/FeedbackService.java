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
    
    @Autowired
    private Group1.ShoesOnlineShop.repository.OrderRepository orderRepository;

    public Page<Feedback> getAll(String status, String keyword, int page, String sort) {

        Pageable pageable = PageRequest.of(page, 5, Sort.by(sort).ascending());

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

    public void saveFeedback(Group1.ShoesOnlineShop.entity.User user, Group1.ShoesOnlineShop.entity.Product product, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        // Check if user has purchased the product
        boolean hasPurchased = orderRepository.hasPurchasedProduct(user.getUserId(), product.getId());
        if (!hasPurchased) {
            throw new IllegalArgumentException("You can only review products you have purchased.");
        }
        
        // Prevent duplicate feedback
        boolean alreadyReviewed = feedbackRepository.existsByUser_UserIdAndProduct_Id(user.getUserId(), product.getId());
        if (alreadyReviewed) {
            throw new IllegalArgumentException("You have already reviewed this product.");
        }

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setProduct(product);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setIsApproved(true);
        feedbackRepository.save(feedback);
    }
}