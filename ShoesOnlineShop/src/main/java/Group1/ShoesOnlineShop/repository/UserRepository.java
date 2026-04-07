package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Kiểm tra xem email nhập vào có bị trùng với tài khoản khác không
    boolean existsByUserEmailAndUserIdNot(String userEmail, Long userId);

    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserName(String userName);
    Optional<User> findByResetToken(String resetToken);
    Optional<User> findByProviderId(String providerId);
    
    java.util.List<User> findByUserRole(String role);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.resetToken = :token, u.resetTokenExpiry = :expiry, u.updatedAt = CURRENT_TIMESTAMP WHERE u.userEmail = :email")
    void updateResetToken(String email, String token, LocalDateTime expiry);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.passwordHash = :passwordHash, u.resetToken = null, u.resetTokenExpiry = null, u.updatedAt = CURRENT_TIMESTAMP WHERE u.userEmail = :email")
    void updatePasswordAndClearToken(String email, String passwordHash);
}