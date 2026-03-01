/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Slider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SliderRepository extends JpaRepository<Slider, Long> {
    // Hàm tìm kiếm theo tên (không phân biệt hoa/thường)
    Page<Slider> findBySliderTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
