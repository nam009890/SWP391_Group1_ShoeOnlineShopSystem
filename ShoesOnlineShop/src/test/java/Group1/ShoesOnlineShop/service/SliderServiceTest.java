package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import Group1.ShoesOnlineShop.repository.SliderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SliderServiceTest {

    @Mock
    private SliderRepository sliderRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Model model;

    @InjectMocks
    private SliderService sliderService;

    // --- Khối 1: Lấy dữ liệu (Get/Read) ---
    @Test
    void testGetSliders_NoFilter() {
        Pageable paging = PageRequest.of(0, 5);
        Page<Slider> expectedPage = new PageImpl<>(Arrays.asList(new Slider(), new Slider()));
        when(sliderRepository.findAll(paging)).thenReturn(expectedPage);

        Page<Slider> result = sliderService.getSliders(null, null, 1, 5);
        assertEquals(2, result.getContent().size());
        verify(sliderRepository, times(1)).findAll(paging);
    }

    @Test
    void testGetSliders_WithKeywordAndStatus() {
        Pageable paging = PageRequest.of(0, 5);
        when(sliderRepository.findBySliderTitleContainingIgnoreCaseAndIsActive("Sale", true, paging)).thenReturn(new PageImpl<>(Collections.emptyList()));

        sliderService.getSliders("Sale", true, 1, 5);
        verify(sliderRepository, times(1)).findBySliderTitleContainingIgnoreCaseAndIsActive("Sale", true, paging);
    }

    @Test
    void testGetSliderById_Found() {
        Slider slider = new Slider();
        slider.setId(1L);
        when(sliderRepository.findById(1L)).thenReturn(Optional.of(slider));

        Slider result = sliderService.getSliderById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // --- Khối 2: Kiểm duyệt Validation ---
    @Test
    void testValidateSlider_ValidData() {
        Slider slider = new Slider();
        slider.setSliderTitle("Valid Title");

        Map<String, String> errors = sliderService.validateSliderLogic(slider, Arrays.asList(1L), Arrays.asList(2L));
        assertTrue(errors.isEmpty(), "Valid data should not return any errors");
    }

    @Test
    void testValidateSliderLogic_DuplicateTitle() {
        Slider slider = new Slider();
        slider.setSliderTitle("Duplicate");
        when(sliderRepository.existsBySliderTitle("Duplicate")).thenReturn(true);

        Map<String, String> errors = sliderService.validateSliderLogic(slider, Arrays.asList(1L), Arrays.asList(2L));
        assertTrue(errors.containsKey("sliderTitle"));
        assertEquals("This Slider title already exists, please choose another!", errors.get("sliderTitle"));
    }

    @Test
    void testValidateSliderLogic_MissingProducts() {
        Slider slider = new Slider();
        slider.setSliderTitle("New Title");

        Map<String, String> errors = sliderService.validateSliderLogic(slider, Arrays.asList(1L), null);
        assertTrue(errors.containsKey("products"));
        assertEquals("Please select at least one product!", errors.get("products"));
    }

    @Test
    void testValidateSliderLogic_MissingCoupons() {
        Slider slider = new Slider();
        slider.setSliderTitle("New Title");

        Map<String, String> errors = sliderService.validateSliderLogic(slider, null, Arrays.asList(1L));
        assertTrue(errors.containsKey("coupons"));
        assertEquals("Please select at least one coupon!", errors.get("coupons"));
    }

    @Test
    void testValidateSlider_MissingImageOnCreate() {
        Slider slider = new Slider();
        slider.setId(null); 
        Map<String, String> errors = sliderService.validateSlider(slider, Arrays.asList(1L), Arrays.asList(1L), null);
        assertTrue(errors.containsKey("imageUrl"));
        assertEquals("Please upload an image!", errors.get("imageUrl"));
    }

    @Test
    void testValidateSlider_InvalidImageFormat() {
        Slider slider = new Slider();
        MockMultipartFile pdfFile = new MockMultipartFile("file", "doc.pdf", "application/pdf", new byte[]{1});
        Map<String, String> errors = sliderService.validateSlider(slider, Arrays.asList(1L), Arrays.asList(1L), pdfFile);
        assertTrue(errors.containsKey("imageUrl"));
        assertEquals("Only image files are allowed!", errors.get("imageUrl"));
    }

    // --- Khối 3: Phục hồi trạng thái (Form Restore) ---
    @Test
    void testRestoreSliderFormState() {
        Slider sliderForm = new Slider();
        sliderForm.setId(1L);

        Product p = new Product();
        p.setProductId(10L);
        when(productRepository.findAllById(Arrays.asList(10L))).thenReturn(Arrays.asList(p));
        
        Coupon c = new Coupon();
        c.setId(20L);
        when(couponRepository.findAllById(Arrays.asList(20L))).thenReturn(Arrays.asList(c));

        Slider existingSlider = new Slider();
        existingSlider.setImageUrl("/old-image.jpg");
        when(sliderRepository.findById(1L)).thenReturn(Optional.of(existingSlider));

        sliderService.restoreSliderFormState(model, sliderForm, Arrays.asList(20L), Arrays.asList(10L), Arrays.asList(5), true);

        verify(model, times(1)).addAttribute(eq("coupons"), any());
        verify(model, times(1)).addAttribute(eq("products"), any());
        
        assertEquals(1, sliderForm.getSliderProducts().size());
        assertEquals(1, sliderForm.getCoupons().size());
        assertEquals("/old-image.jpg", sliderForm.getImageUrl());
    }

    // --- Khối 4: Lưu/Thao tác Database ---
    @Test
    void testProcessAndSaveSlider_CreateNew() throws Exception {
        Slider sliderForm = new Slider();
        sliderForm.setSliderTitle("New Slider");

        MockMultipartFile image = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{1});

        sliderService.processAndSaveSlider(sliderForm, Arrays.asList(1L), Arrays.asList(2L), Arrays.asList(10), image);

        verify(sliderRepository, times(1)).save(any(Slider.class));
    }

    @Test
    void testProcessAndSaveSlider_UpdateExisting() throws Exception {
        Slider sliderForm = new Slider();
        sliderForm.setId(1L);
        sliderForm.setSliderTitle("Updated Title");

        Slider dbSlider = new Slider();
        when(sliderRepository.findById(1L)).thenReturn(Optional.of(dbSlider));

        sliderService.processAndSaveSlider(sliderForm, Arrays.asList(1L), Arrays.asList(2L), Arrays.asList(10), null);

        verify(sliderRepository, times(1)).save(dbSlider);
        assertEquals("Updated Title", dbSlider.getSliderTitle());
    }

    @Test
    void testDeleteSlider() {
        sliderService.deleteSlider(1L);
        verify(sliderRepository, times(1)).deleteById(1L);
    }
}