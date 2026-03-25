package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser_UserId(Long userId);

    List<Cart> findBySessionId(String sessionId);

    Cart findByUser_UserIdAndProduct_IdAndSizeAndColor(Long userId, Long productId, String size, String color);

    Cart findBySessionIdAndProduct_IdAndSizeAndColor(String sessionId, Long productId, String size, String color);

    void deleteByUser_UserId(Long userId);

    void deleteBySessionId(String sessionId);
}
