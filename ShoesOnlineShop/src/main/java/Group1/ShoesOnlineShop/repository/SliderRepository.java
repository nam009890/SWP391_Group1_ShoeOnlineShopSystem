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
    
    long countByIsActive(Boolean isActive);
    java.util.List<Slider> findTop5ByOrderByCreatedAtDesc();
    java.util.List<Slider> findTop50ByOrderByCreatedAtDesc();

    // Active sliders for home page carousel
    java.util.List<Slider> findByIsActiveTrueOrderByCreatedAtDesc();
<<<<<<< HEAD

    // Active + Approved sliders for homepage (only show approved content)
    java.util.List<Slider> findByIsActiveTrueAndApprovalStatusOrderByCreatedAtDesc(String approvalStatus);
    
    Page<Slider> findByApprovalStatus(String approvalStatus, Pageable pageable);
    Page<Slider> findByApprovalStatusAndSliderTitleContainingIgnoreCase(String approvalStatus, String keyword, Pageable pageable);
=======
    
    Page<Slider> findByApprovalStatus(String approvalStatus, Pageable pageable);
>>>>>>> 088cea8310666489ea9c06a81f5a59706a724daa
}