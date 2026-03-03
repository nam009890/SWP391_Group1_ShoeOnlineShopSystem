package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Slider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Long> {
    Page<Slider> findBySliderTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // Validate: Tên slider đã tồn tại (Lúc Create)
    boolean existsBySliderTitle(String sliderTitle);

    // Validate: Tên slider đã tồn tại (Lúc Update, bỏ qua ID hiện tại)
    boolean existsBySliderTitleAndIdNot(String sliderTitle, Long id);
}