package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>,JpaSpecificationExecutor<Coupon> {
    
    Page<Coupon> findByCouponNameContainingIgnoreCaseOrCouponCodeContainingIgnoreCase(String name, String code, Pageable pageable);

    // Kiểm tra lúc Create (Tạo mới)
    boolean existsByCouponName(String couponName);
    boolean existsByCouponCode(String couponCode);

    // Kiểm tra lúc Update (Bỏ qua ID hiện tại)
    boolean existsByCouponNameAndIdNot(String couponName, Long id);
    boolean existsByCouponCodeAndIdNot(String couponCode, Long id);
    
    long countByIsActive(Boolean isActive);
    java.util.List<Coupon> findTop5ByOrderByCreatedAtDesc();
    java.util.List<Coupon> findTop50ByOrderByCreatedAtDesc();
    
    Page<Coupon> findByApprovalStatus(String approvalStatus, Pageable pageable);
}