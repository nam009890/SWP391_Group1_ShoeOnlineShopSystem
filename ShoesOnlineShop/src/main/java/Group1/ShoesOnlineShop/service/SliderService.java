package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Product;
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

@Service
public class SliderService {

    @Autowired
    private SliderRepository sliderRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private ProductRepository productRepository;

    // Đã thêm tham số isActive vào đây
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
            slider.setCoupons(new java.util.ArrayList<>());
        }

        slider.getSliderProducts().clear();
        if (productIds != null && !productIds.isEmpty()) {
            List<Product> products = productRepository.findAllById(productIds);
            for (Product p : products) {
                slider.addProduct(p, 0); // Default discount 0 for tests/legacy logic
            }
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

        // Validate Logic DB
        if (sliderForm.getSliderTitle() != null && isSliderTitleExists(sliderForm.getSliderTitle(), sliderForm.getId())) {
            errors.put("sliderTitle", "This Slider title already exists, please choose another!");
        }
        if (productIds == null || productIds.isEmpty()) {
            errors.put("products", "Please select at least one product!");
        }
        if (couponIds == null || couponIds.isEmpty()) {
            errors.put("coupons", "Please select at least one coupon!");
        }

        // Validate File Ảnh
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

    // 2. HÀM XỬ LÝ LƯU FILE VÀ GHI DATABASE
    public void processAndSaveSlider(Slider sliderForm, List<Long> couponIds, List<Long> productIds, List<Integer> productDiscounts, MultipartFile imageFile) throws IOException {
        Slider targetSlider;
        
        // Lấy Slider cũ ra (nếu Update) hoặc tạo mới (nếu Create)
        if (sliderForm.getId() != null) {
            targetSlider = sliderRepository.findById(sliderForm.getId()).orElse(new Slider());
        } else {
            targetSlider = new Slider();
            targetSlider.setCreatedAt(LocalDateTime.now());
        }

        // Cập nhật các trường thông tin cơ bản
        targetSlider.setSliderTitle(sliderForm.getSliderTitle());
        targetSlider.setIsActive(sliderForm.getIsActive() != null ? sliderForm.getIsActive() : false);
        targetSlider.setUpdatedAt(LocalDateTime.now());

        // Xử lý lưu File Ảnh vào hệ thống
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
            Path uploadPath = Paths.get("src/main/resources/static/uploads/");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                targetSlider.setImageUrl("/uploads/" + fileName);
            }
        }

        // Xử lý lưu Coupon và Product đính kèm
        if (couponIds != null && !couponIds.isEmpty()) {
            targetSlider.setCoupons(couponRepository.findAllById(couponIds));
        } else {
            targetSlider.setCoupons(new java.util.ArrayList<>());
        }

        targetSlider.getSliderProducts().clear();
        if (productIds != null && !productIds.isEmpty()) {
            List<Product> products = productRepository.findAllById(productIds);
            for (int i = 0; i < productIds.size(); i++) {
                Long pId = productIds.get(i);
                Integer discount = (productDiscounts != null && i < productDiscounts.size()) ? productDiscounts.get(i) : 0;
                Product p = products.stream().filter(prod -> prod.getProductId().equals(pId)).findFirst().orElse(null);
                if (p != null) {
                    targetSlider.addProduct(p, discount);
                }
            }
        }

        // Lưu vào DB
        sliderRepository.save(targetSlider);
    }
}