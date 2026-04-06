package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.entity.UserCoupon;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.UserCouponRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserCouponService {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Transactional
    public boolean saveCoupon(Long userId, Long couponId) {
        if (userCouponRepository.existsByUser_UserIdAndCoupon_Id(userId, couponId)) {
            return false;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new RuntimeException("COUPON_INACTIVE");
        }

        // Check quantity limit
        if (coupon.getQuantity() != null && coupon.getUsedCount() >= coupon.getQuantity()) {
            throw new RuntimeException("COUPON_OUT_OF_STOCK");
        }

        LocalDate today = LocalDate.now();
        if (coupon.getCreateDate().isAfter(today) || coupon.getEndDate().isBefore(today)) {
            throw new RuntimeException("COUPON_EXPIRED");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setUsed(false);

        userCouponRepository.save(userCoupon);
        return true;
    }

    public List<UserCoupon> getAvailableCoupons(Long userId) {
        return userCouponRepository.findByUser_UserIdAndIsUsedFalse(userId);
    }

    @Transactional
    public void markAsUsed(Long userId, Long couponId) {
        Optional<UserCoupon> userCouponOpt = userCouponRepository.findByUser_UserIdAndCoupon_Id(userId, couponId);
        if (userCouponOpt.isPresent()) {
            UserCoupon userCoupon = userCouponOpt.get();
            userCoupon.setUsed(true);
            userCouponRepository.save(userCoupon);
        }
    }
}
