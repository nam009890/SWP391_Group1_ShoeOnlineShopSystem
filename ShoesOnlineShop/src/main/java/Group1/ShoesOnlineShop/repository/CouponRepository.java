/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Group1.ShoesOnlineShop.repository;
import Group1.ShoesOnlineShop.entity.Coupon; // <-- Thay bằng đường dẫn chuẩn
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    // Tìm kiếm có phân trang (trả về Page thay vì List)
    Page<Coupon> findByCouponNameContainingIgnoreCaseOrCouponCodeContainingIgnoreCase(String name, String code, Pageable pageable);
}
