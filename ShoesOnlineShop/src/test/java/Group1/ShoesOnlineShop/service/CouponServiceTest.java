package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    // --- Khối 1: Lấy & Lọc Dữ liệu ---
    @Test
    void testGetCoupons_WithFilters() {
        Pageable paging = PageRequest.of(0, 5);
        Page<Coupon> expectedPage = new PageImpl<>(Arrays.asList(new Coupon()));
        when(couponRepository.findAll(any(Specification.class), eq(paging))).thenReturn(expectedPage);

        Page<Coupon> result = couponService.getCoupons("TET", 10, true, "VALID", 1, 5);
        assertEquals(1, result.getContent().size());
        verify(couponRepository, times(1)).findAll(any(Specification.class), eq(paging));
    }

    @Test
    void testGetActiveAndValidCoupons() {
        Coupon c1 = new Coupon(); // Valid
        c1.setIsActive(true);
        c1.setCreateDate(LocalDate.now().minusDays(1));
        c1.setEndDate(LocalDate.now().plusDays(5));

        Coupon c2 = new Coupon(); // Inactive
        c2.setIsActive(false);
        c2.setCreateDate(LocalDate.now().minusDays(1));
        c2.setEndDate(LocalDate.now().plusDays(5));

        Coupon c3 = new Coupon(); // Expired
        c3.setIsActive(true);
        c3.setCreateDate(LocalDate.now().minusDays(10));
        c3.setEndDate(LocalDate.now().minusDays(1));

        when(couponRepository.findAll()).thenReturn(Arrays.asList(c1, c2, c3));

        List<Coupon> result = couponService.getActiveAndValidCoupons();
        assertEquals(1, result.size());
        assertTrue(result.contains(c1));
    }

    @Test
    void testGetCouponById_Found() {
        Coupon coupon = new Coupon();
        coupon.setId(10L);
        when(couponRepository.findById(10L)).thenReturn(Optional.of(coupon));

        Coupon result = couponService.getCouponById(10L);
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    // --- Khối 2: Kiểm duyệt (Validation) ---
    @Test
    void testValidateLogic_Success() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("SUMMER SALE");
        coupon.setCouponCode("SUMMER2026");
        coupon.setCreateDate(LocalDate.now().plusDays(1));
        coupon.setEndDate(LocalDate.now().plusDays(10));

        Map<String, String> errors = couponService.validateCouponLogic(coupon);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateLogic_DuplicateName() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("SUMMER SALE");
        when(couponRepository.existsByCouponName("SUMMER SALE")).thenReturn(true);

        Map<String, String> errors = couponService.validateCouponLogic(coupon);
        assertTrue(errors.containsKey("couponName"));
        assertEquals("This Coupon name already exists in the system!", errors.get("couponName"));
    }

    @Test
    void testValidateLogic_DuplicateCode() {
        Coupon coupon = new Coupon();
        coupon.setCouponCode("CODE100");
        when(couponRepository.existsByCouponCode("CODE100")).thenReturn(true);

        Map<String, String> errors = couponService.validateCouponLogic(coupon);
        assertTrue(errors.containsKey("couponCode"));
        assertEquals("This Coupon Code is already in use!", errors.get("couponCode"));
    }

    @Test
    void testValidateLogic_EndDateBeforeStartDate() {
        Coupon coupon = new Coupon();
        coupon.setCreateDate(LocalDate.now().plusDays(5));
        coupon.setEndDate(LocalDate.now().plusDays(2)); // end before start

        Map<String, String> errors = couponService.validateCouponLogic(coupon);
        assertTrue(errors.containsKey("endDate"));
        assertEquals("The end date must be after or equal to the start date!", errors.get("endDate"));
    }

    // --- Khối 3: Lưu/Xoá DB ---
    @Test
    void testSaveCoupon_Success() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("TET 2026");

        couponService.saveCoupon(coupon);
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void testDeleteCoupon() {
        couponService.deleteCoupon(1L);
        verify(couponRepository, times(1)).deleteById(1L);
    }
}