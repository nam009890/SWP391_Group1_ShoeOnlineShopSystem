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

        if (productIds != null && !productIds.isEmpty()) {
            slider.setProducts(productRepository.findAllById(productIds));
        } else {
            slider.setProducts(new java.util.ArrayList<>());
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
}