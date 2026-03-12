package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByUserEmail(String userEmail);

    boolean existsByUserEmailAndUserIdNot(String userEmail, Long userId);

    boolean existsByUserName(String userName);

    boolean existsByUserNameAndUserIdNot(String userName, Long userId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
}
