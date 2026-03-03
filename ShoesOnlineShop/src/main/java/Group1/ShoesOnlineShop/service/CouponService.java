package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Page<Coupon> getCoupons(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return couponRepository.findAll(paging);
        } else {
            return couponRepository.findByCouponNameContainingIgnoreCaseOrCouponCodeContainingIgnoreCase(keyword, keyword, paging);
        }
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
}