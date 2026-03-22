package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.UserCouponRepository;
import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.entity.UserCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Coupon> getCoupons(String keyword, Integer discount, Boolean status, String validity, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);

        // Tạo một Specification để "lắp ráp" các bộ lọc động
        Specification<Coupon> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. LỌC THEO TỪ KHÓA (Tên hoặc Mã)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.toLowerCase() + "%";
                Predicate nameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("couponName")), likeKeyword);
                Predicate codeMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("couponCode")), likeKeyword);
                predicates.add(criteriaBuilder.or(nameMatch, codeMatch));
            }

            // 2. LỌC THEO DISCOUNT
            if (discount != null) {
                predicates.add(criteriaBuilder.equal(root.get("discountPercent"), discount));
            }

            // 3. LỌC THEO TRẠNG THÁI ACTIVE
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), status));
            }

            // 4. LỌC THEO THỜI HẠN (Còn hạn / Hết hạn)
            if (validity != null && !validity.isEmpty()) {
                LocalDate today = LocalDate.now();
                if ("VALID".equalsIgnoreCase(validity)) {
                    // Còn hạn: Hôm nay >= Ngày bắt đầu VÀ Hôm nay <= Ngày kết thúc
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), today));
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), today));
                } else if ("EXPIRED".equalsIgnoreCase(validity)) {
                    // Hết hạn: Hôm nay > Ngày kết thúc HOẶC Hôm nay < Ngày bắt đầu (chưa tới hạn)
                    Predicate expired = criteriaBuilder.lessThan(root.get("endDate"), today);
                    Predicate upcoming = criteriaBuilder.greaterThan(root.get("createDate"), today);
                    predicates.add(criteriaBuilder.or(expired, upcoming));
                }
            }

            // Gộp tất cả các mảnh lego (predicates) lại bằng phép AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Truyền spec đã lắp ráp vào repository để tìm kiếm
        return couponRepository.findAll(spec, paging);
    }

    // Save Coupon (Used for both Create and Update)
    public void saveCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }

    // Get 1 Coupon by ID (Used to display on the Update form)
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    // Delete Coupon
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    // Check for duplicate Name
    public boolean isCouponNameExists(String name, Long id) {
        if (id == null) {
            return couponRepository.existsByCouponName(name);
        }
        return couponRepository.existsByCouponNameAndIdNot(name, id);
    }

    // Check for duplicate Code
    public boolean isCouponCodeExists(String code, Long id) {
        if (id == null) {
            return couponRepository.existsByCouponCode(code);
        }
        return couponRepository.existsByCouponCodeAndIdNot(code, id);
    }

    // ==========================================
    // NEW METHOD: Extract all Validation logic here
    // ==========================================
    public Map<String, String> validateCouponLogic(Coupon coupon) {
        Map<String, String> errors = new HashMap<>();

        // 1. Coupon name already exists in DB
        if (coupon.getCouponName() != null && isCouponNameExists(coupon.getCouponName(), coupon.getId())) {
            errors.put("couponName", "This Coupon name already exists in the system!");
        }

        // 2. Coupon code already exists in DB
        if (coupon.getCouponCode() != null && isCouponCodeExists(coupon.getCouponCode(), coupon.getId())) {
            errors.put("couponCode", "This Coupon Code is already in use!");
        }

        // 3. Start date is after end date (or end date is before start date)
        if (coupon.getCreateDate() != null && coupon.getEndDate() != null) {
            if (coupon.getEndDate().isBefore(coupon.getCreateDate())) {
                errors.put("endDate", "The end date must be after or equal to the start date!");
            }
        }

        return errors;
    }
    // Get active coupons for home page
    public java.util.List<Group1.ShoesOnlineShop.entity.Coupon> getActiveCoupons() {
        return getCoupons("", null, true, "VALID", 1, 10).getContent();
    }

    // Save coupon for user
    public void saveCouponForUser(Long userId, Long couponId) {
        if (!userCouponRepository.existsByUser_UserIdAndCoupon_Id(userId, couponId)) {
            userRepository.findById(userId).ifPresent(user -> {
                couponRepository.findById(couponId).ifPresent(coupon -> {
                    userCouponRepository.save(new UserCoupon(user, coupon));
                });
            });
        }
    }
}

