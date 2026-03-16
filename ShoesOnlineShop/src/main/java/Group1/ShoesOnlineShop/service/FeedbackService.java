package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Feedback;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.entity.Order;
import Group1.ShoesOnlineShop.repository.FeedbackRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    public List<Feedback> getFeedbacksByProduct(Long productId) {
        return feedbackRepository.findByProduct_IdOrderByCreatedAtDesc(productId);
    }

    public boolean hasUserSubmittedFeedbackForOrder(Long orderId, Long productId) {
        return feedbackRepository.existsByOrderOrderIdAndProduct_Id(orderId, productId);
    }

    public void submitFeedback(Long userId, Long productId, Long orderId, Integer rating, String comments) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        Order order = orderRepository.findById(orderId).orElse(null);

        if (user != null && product != null && order != null) {
            Feedback feedback = new Feedback();
            feedback.setUser(user);
            feedback.setProduct(product);
            feedback.setOrder(order);
            feedback.setRating(rating);
            feedback.setComment(comments);
            feedbackRepository.save(feedback);
        }
    }
}
