package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUser_UserIdAndIsUsedFalse(Long userId);

    boolean existsByUser_UserIdAndCoupon_Id(Long userId, Long couponId);

    Optional<UserCoupon> findByUser_UserIdAndCoupon_Id(Long userId, Long couponId);
}
