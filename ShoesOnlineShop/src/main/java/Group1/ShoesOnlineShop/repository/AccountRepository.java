package Group1.ShoesOnlineShop.repository;



import Group1.ShoesOnlineShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<User, Long> {
    // Spring Data JPA sẽ tự động dịch câu này thành: SELECT * FROM accounts WHERE username = ?
    Optional<User> findByuserName(String username);
}
