package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.repository.SliderRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SliderServiceTest {

    @Mock
    private SliderRepository sliderRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SliderService sliderService;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // 1. Test Successful Creation
    @Test
    void testSaveSlider_Success() {
        Slider slider = new Slider();
        slider.setSliderTitle("Exciting Summer");
        List<Long> couponIds = Arrays.asList(1L, 2L);
        List<Long> productIds = Arrays.asList(3L, 4L);

        sliderService.saveSlider(slider, couponIds, productIds);

        verify(sliderRepository, times(1)).save(slider);
        verify(couponRepository, times(1)).findAllById(couponIds);
        verify(productRepository, times(1)).findAllById(productIds);
    }

    // 2. Test Duplicate Slider Title
    @Test
    void testValidateLogic_DuplicateTitle() {
        Slider slider = new Slider();
        slider.setSliderTitle("Summer");

        when(sliderRepository.existsBySliderTitle("Summer")).thenReturn(true);

        Map<String, String> errors = sliderService.validateSliderLogic(slider, Arrays.asList(1L), Arrays.asList(2L));

        assertFalse(errors.isEmpty());
        assertEquals("This Slider title already exists, please choose another!", errors.get("sliderTitle"));
    }

    // 3. Test Error: No Product Selected
    @Test
    void testValidateLogic_MissingProducts() {
        Slider slider = new Slider();
        slider.setSliderTitle("Winter");

        Map<String, String> errors = sliderService.validateSliderLogic(slider, Arrays.asList(1L), Collections.emptyList());

        assertTrue(errors.containsKey("products"));
        assertEquals("Please select at least one product!", errors.get("products"));
    }

    // 4. Test Error: No Coupon Selected
    @Test
    void testValidateLogic_MissingCoupons() {
        Slider slider = new Slider();
        slider.setSliderTitle("Winter");

        Map<String, String> errors = sliderService.validateSliderLogic(slider, null, Arrays.asList(1L));

        assertTrue(errors.containsKey("coupons"));
        assertEquals("Please select at least one coupon!", errors.get("coupons"));
    }

    // 5. Test Error: Blank Title
    @Test
    void testValidateEntity_BlankTitle() {
        Slider slider = new Slider();
        slider.setSliderTitle("");
        slider.setImageUrl("http://image.com/pic.jpg");
        slider.setLinkUrl("http://link.com");
        slider.setPosition(1);

        Set<ConstraintViolation<Slider>> violations = validator.validate(slider);
        
        boolean hasTitleError = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sliderTitle"));
        assertTrue(hasTitleError, "Must report an error when Slider Title is blank");
    }

    // 6. Test Error: Blank Image URL
    @Test
void testValidateEntity_BlankImageUrl() {
    Slider slider = new Slider();
    slider.setSliderTitle("Valid Title");
    slider.setLinkUrl("http://example.com");
    slider.setPosition(1);
    slider.setImageUrl(""); // Cố tình để trống

    Set<ConstraintViolation<Slider>> violations = validator.validate(slider);
    
    // SỬA LẠI DÒNG NÀY: Thay vì mong đợi có lỗi, bây giờ ta mong đợi KHÔNG có lỗi (vì đã dời lên Controller)
    assertTrue(violations.isEmpty(), "Entity must NOT report an error because image validation is moved to Controller");
}
}