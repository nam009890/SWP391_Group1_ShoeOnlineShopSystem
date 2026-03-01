package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(name = "coupon_name", nullable = false, length = 200)
    private String couponName;

    @Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 1. Constructor không tham số (Bắt buộc)
    public Coupon() {
    }

    // 2. Constructor có tham số (Tùy chọn dùng cho test)
    public Coupon(Long id, String couponName, String couponCode, Integer discountPercent, LocalDate createDate, LocalDate endDate, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.couponName = couponName;
        this.couponCode = couponCode;
        this.discountPercent = discountPercent;
        this.createDate = createDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // 3. GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
}