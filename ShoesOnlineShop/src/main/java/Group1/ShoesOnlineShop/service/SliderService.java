package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.repository.SliderRepository;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class SliderService {

    @Autowired
    private SliderRepository sliderRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ProductRepository productRepository;

    public Page<Slider> getSliders(String keyword, Boolean isActive, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        
        if (isActive != null) {
            if (keyword != null && !keyword.isEmpty()) {
                return sliderRepository.findBySliderTitleContainingIgnoreCaseAndIsActive(keyword, isActive, paging);
            }
            return sliderRepository.findByIsActive(isActive, paging);
        } else {
            if (keyword != null && !keyword.isEmpty()) {
                return sliderRepository.findBySliderTitleContainingIgnoreCase(keyword, paging);
            }
            return sliderRepository.findAll(paging);
        }
    }

    public void saveSlider(Slider slider, List<Long> couponIds, List<Long> productIds) {
        if (slider.getCreatedAt() == null) {
            slider.setCreatedAt(java.time.LocalDateTime.now());
        }
        slider.setUpdatedAt(java.time.LocalDateTime.now());

        if (couponIds != null && !couponIds.isEmpty()) {
            slider.setCoupons(couponRepository.findAllById(couponIds));
        } else {
            slider.setCoupons(new ArrayList<>());
        }

        if (productIds != null && !productIds.isEmpty()) {
            slider.setProducts(productRepository.findAllById(productIds));
        } else {
            slider.setProducts(new ArrayList<>());
        }

        sliderRepository.save(slider);
    }

    public Slider getSliderById(Long id) {
        return sliderRepository.findById(id).orElse(null);
    }

    public void deleteSlider(Long id) {
        sliderRepository.deleteById(id);
    }

    public boolean isSliderTitleExists(String title, Long id) {
        if (id == null) {
            return sliderRepository.existsBySliderTitle(title);
        }
        return sliderRepository.existsBySliderTitleAndIdNot(title, id);
    }

    public Map<String, String> validateSliderLogic(Slider slider, List<Long> couponIds, List<Long> productIds) {
        Map<String, String> errors = new HashMap<>();

        if (slider.getSliderTitle() != null && isSliderTitleExists(slider.getSliderTitle(), slider.getId())) {
            errors.put("sliderTitle", "This Slider title already exists, please choose another!");
        }
        if (productIds == null || productIds.isEmpty()) {
            errors.put("products", "Please select at least one product!");
        }
        if (couponIds == null || couponIds.isEmpty()) {
            errors.put("coupons", "Please select at least one coupon!");
        }
        return errors;
    }

    public Map<String, String> validateSlider(Slider sliderForm, List<Long> couponIds, List<Long> productIds, MultipartFile imageFile) {
        Map<String, String> errors = new HashMap<>();
        boolean isUpdate = (sliderForm.getId() != null);

        if (sliderForm.getSliderTitle() != null && isSliderTitleExists(sliderForm.getSliderTitle(), sliderForm.getId())) {
            errors.put("sliderTitle", "This Slider title already exists, please choose another!");
        }
        if (productIds == null || productIds.isEmpty()) {
            errors.put("products", "Please select at least one product!");
        }
        if (couponIds == null || couponIds.isEmpty()) {
            errors.put("coupons", "Please select at least one coupon!");
        }

        if (!isUpdate && (imageFile == null || imageFile.isEmpty())) {
            errors.put("imageUrl", "Please upload an image!");
        } else if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                errors.put("imageUrl", "Only image files are allowed!");
            } else if (imageFile.getSize() > 5 * 1024 * 1024) {
                errors.put("imageUrl", "Image size must be less than 5MB!");
            } else {
                try {
                    BufferedImage img = ImageIO.read(imageFile.getInputStream());
                    if (img != null && img.getWidth() < 1000) {
                        errors.put("imageUrl", "Image width must be at least 1000px for a standard slider!");
                    }
                } catch (Exception e) {
                    errors.put("imageUrl", "Corrupted image file.");
                }
            }
        }
        return errors;
    }

    public void processAndSaveSlider(Slider sliderForm, List<Long> couponIds, List<Long> productIds, MultipartFile imageFile) throws IOException {
        Slider targetSlider;
        if (sliderForm.getId() != null) {
            targetSlider = sliderRepository.findById(sliderForm.getId()).orElse(new Slider());
        } else {
            targetSlider = new Slider();
            targetSlider.setCreatedAt(LocalDateTime.now());
        }

        targetSlider.setSliderTitle(sliderForm.getSliderTitle());
        targetSlider.setIsActive(sliderForm.getIsActive() != null ? sliderForm.getIsActive() : false);
        targetSlider.setUpdatedAt(LocalDateTime.now());

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadPath = Paths.get("src/main/resources/static/uploads/");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                targetSlider.setImageUrl("/uploads/" + fileName);
            }
        }

        if (couponIds != null && !couponIds.isEmpty()) {
            targetSlider.setCoupons(couponRepository.findAllById(couponIds));
        } else {
            targetSlider.setCoupons(new ArrayList<>());
        }

        if (productIds != null && !productIds.isEmpty()) {
            targetSlider.setProducts(productRepository.findAllById(productIds));
        } else {
            targetSlider.setProducts(new ArrayList<>());
        }

        sliderRepository.save(targetSlider);
    }

    public List<Slider> getActiveSliders() {
        return getMockSliders();
    }

    private List<Slider> getMockSliders() {
        List<Slider> mockSliders = new ArrayList<>();
        
        mockSliders.add(createMockSlider(1L, "New Year Sale 2026", "/images/slider1.jpg", 1));
        mockSliders.add(createMockSlider(2L, "Summer Collection", "/images/slider2.jpg", 2));
        mockSliders.add(createMockSlider(3L, "Jordan Special Offer", "/images/slider3.jpg", 3));
        mockSliders.add(createMockSlider(4L, "Running Shoes Discount", "/images/slider4.jpg", 4));
        mockSliders.add(createMockSlider(5L, "Vietnam Brand Biti's", "/images/slider5.jpg", 5));
        
        return mockSliders;
    }

    private Slider createMockSlider(Long id, String title, String imageUrl, Integer position) {
        Slider s = new Slider();
        s.setId(id);
        s.setSliderTitle(title);
        s.setImageUrl(imageUrl);
        s.setPosition(position);
        s.setIsActive(true);
        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());
        return s;
    }
}