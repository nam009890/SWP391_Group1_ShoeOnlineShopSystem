package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findBySessionId(String sessionId);
    List<Cart> findByUserUserId(Long userId);
    Cart findBySessionIdAndProduct_IdAndSelectedColorAndSelectedSize(String sessionId, Long productId, String selectedColor, String selectedSize);
    Cart findByUserUserIdAndProduct_IdAndSelectedColorAndSelectedSize(Long userId, Long productId, String selectedColor, String selectedSize);
    void deleteBySessionId(String sessionId);
    void deleteByUserUserId(Long userId);
}
