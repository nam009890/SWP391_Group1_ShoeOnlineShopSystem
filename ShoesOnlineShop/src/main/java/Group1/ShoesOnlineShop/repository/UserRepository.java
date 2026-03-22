package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Kiểm tra xem email nhập vào có bị trùng với tài khoản khác không
    boolean existsByUserEmailAndUserIdNot(String userEmail, Long userId);

    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserName(String userName);
}