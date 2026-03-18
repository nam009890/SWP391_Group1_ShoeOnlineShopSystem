package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Slider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Long> {
    Page<Slider> findBySliderTitleContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Thêm hàm lọc theo cả từ khóa và trạng thái
    Page<Slider> findBySliderTitleContainingIgnoreCaseAndIsActive(String keyword, Boolean isActive, Pageable pageable);
    
    // Thêm hàm lọc chỉ theo trạng thái
    Page<Slider> findByIsActive(Boolean isActive, Pageable pageable);

    boolean existsBySliderTitle(String sliderTitle);
    boolean existsBySliderTitleAndIdNot(String sliderTitle, Long id);
}