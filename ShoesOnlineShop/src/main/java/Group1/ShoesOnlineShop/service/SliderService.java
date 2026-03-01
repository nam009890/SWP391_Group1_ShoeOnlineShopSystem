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
import java.util.List;

@Service
public class SliderService {

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<Slider> getSliders(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return sliderRepository.findAll(paging);
        } else {
            return sliderRepository.findBySliderTitleContainingIgnoreCase(keyword, paging);
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
}