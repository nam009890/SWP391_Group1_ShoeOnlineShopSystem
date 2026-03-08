package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // 1. SUCCESSFUL - Test saving Coupon successfully
    @Test
    void testSaveCoupon_Success() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("TET 2026");
        coupon.setCouponCode("TET26");
        coupon.setDiscountPercent(15);

        couponService.saveCoupon(coupon);
        verify(couponRepository, times(1)).save(coupon);
    }

    // 2. COUPON NAME ALREADY EXISTS IN DB
    @Test
    void testValidateLogic_DuplicateName() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("SUMMER SALE");

        when(couponRepository.existsByCouponName("SUMMER SALE")).thenReturn(true);

        Map<String, String> errors = couponService.validateCouponLogic(coupon);

        assertFalse(errors.isEmpty());
        assertEquals("This Coupon name already exists in the system!", errors.get("couponName"));
    }

    // 3. COUPON CODE ALREADY EXISTS IN DB
    @Test
    void testValidateLogic_DuplicateCode() {
        Coupon coupon = new Coupon();
        coupon.setCouponCode("CODE100");

        when(couponRepository.existsByCouponCode("CODE100")).thenReturn(true);

        Map<String, String> errors = couponService.validateCouponLogic(coupon);

        assertFalse(errors.isEmpty());
        assertEquals("This Coupon Code is already in use!", errors.get("couponCode"));
    }

    // 4. START DATE IS AFTER END DATE
    @Test
    void testValidateLogic_EndDateBeforeStartDate() {
        Coupon coupon = new Coupon();
        coupon.setCreateDate(LocalDate.now().plusDays(5));
        coupon.setEndDate(LocalDate.now().plusDays(2));

        Map<String, String> errors = couponService.validateCouponLogic(coupon);

        assertFalse(errors.isEmpty());
        assertEquals("The end date must be after or equal to the start date!", errors.get("endDate"));
    }

    // 5. BLANK COUPON NAME
    @Test
    void testValidateEntity_BlankName() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("");

        Set<ConstraintViolation<Coupon>> violations = validator.validateProperty(coupon, "couponName");
        assertFalse(violations.isEmpty());
        assertEquals("Coupon name cannot be blank!", violations.iterator().next().getMessage());
    }

    // 6. BLANK COUPON CODE
    @Test
    void testValidateEntity_BlankCode() {
        Coupon coupon = new Coupon();
        coupon.setCouponCode(null);

        Set<ConstraintViolation<Coupon>> violations = validator.validateProperty(coupon, "couponCode");
        assertFalse(violations.isEmpty());
        assertEquals("Coupon code cannot be blank!", violations.iterator().next().getMessage());
    }

    // 7. NULL DISCOUNT
    @Test
    void testValidateEntity_NullDiscount() {
        Coupon coupon = new Coupon();
        coupon.setDiscountPercent(null);

        Set<ConstraintViolation<Coupon>> violations = validator.validateProperty(coupon, "discountPercent");
        assertFalse(violations.isEmpty());
    }

    // 8. NULL START DATE
    @Test
    void testValidateEntity_NullStartDate() {
        Coupon coupon = new Coupon();
        coupon.setCreateDate(null);

        Set<ConstraintViolation<Coupon>> violations = validator.validateProperty(coupon, "createDate");
        assertFalse(violations.isEmpty());
        assertEquals("Please select a start date!", violations.iterator().next().getMessage());
    }

    // 9. NULL END DATE
    @Test
    void testValidateEntity_NullEndDate() {
        Coupon coupon = new Coupon();
        coupon.setEndDate(null);

        Set<ConstraintViolation<Coupon>> violations = validator.validateProperty(coupon, "endDate");
        assertFalse(violations.isEmpty());
        assertEquals("Please select an end date!", violations.iterator().next().getMessage());
    }

    // 10. START DATE IN THE PAST
    @Test
    void testValidateEntity_PastStartDate() {
        Coupon coupon = new Coupon();
        coupon.setCreateDate(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<Coupon>> violations = validator.validateProperty(coupon, "createDate");
        assertFalse(violations.isEmpty());
        assertEquals("Start date cannot be in the past!", violations.iterator().next().getMessage());
    }

    // 11. END DATE IN THE PAST
    @Test
    void testValidateEntity_PastEndDate() {
        Coupon coupon = new Coupon();
        coupon.setEndDate(LocalDate.now().minusDays(10));

        Set<ConstraintViolation<Coupon>> violations = validator.validateProperty(coupon, "endDate");
        assertFalse(violations.isEmpty());
        assertEquals("End date cannot be in the past!", violations.iterator().next().getMessage());
    }
    
    // 12. LOGIC TEST: BOTH DATES ARE NULL
    @Test
    void testValidateLogic_BlankBothDates() {
        Coupon coupon = new Coupon();
        coupon.setCreateDate(null);
        coupon.setEndDate(null);

        Map<String, String> errors = couponService.validateCouponLogic(coupon);
        assertFalse(errors.containsKey("endDate"));
    }
    
    // 13. ENTITY TEST: BOTH DATES ARE NULL AT THE SAME TIME
    @Test
    void testValidateEntity_BothDatesNull() {
        Coupon coupon = new Coupon();
        coupon.setCreateDate(null);
        coupon.setEndDate(null);

        Set<ConstraintViolation<Coupon>> violations = validator.validate(coupon);

        boolean hasStartDateError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("createDate"));
        boolean hasEndDateError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("endDate"));

        assertTrue(hasStartDateError, "Must report an error when start date is blank!");
        assertTrue(hasEndDateError, "Must report an error when end date is blank!");
    }
}