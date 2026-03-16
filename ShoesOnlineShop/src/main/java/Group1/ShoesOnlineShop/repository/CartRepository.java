package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findBySessionId(String sessionId);
    List<Cart> findByUserUserId(Long userId);
    Cart findBySessionIdAndProduct_Id(String sessionId, Long productId);
    Cart findByUserUserIdAndProduct_Id(Long userId, Long productId);
    void deleteBySessionId(String sessionId);
    void deleteByUserUserId(Long userId);
}
