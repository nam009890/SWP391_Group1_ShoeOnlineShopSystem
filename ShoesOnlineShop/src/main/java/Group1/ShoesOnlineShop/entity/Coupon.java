package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    // 1. Blank coupon name
    @NotBlank(message = "Coupon name cannot be blank!")
    @Column(name = "coupon_name", nullable = false, length = 200)
    private String couponName;

    // 2. Blank coupon code
    @NotBlank(message = "Coupon code cannot be blank!")
    @Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;

    // 3. No discount selected (Added min/max for stricter validation)
    @NotNull(message = "Please enter a discount value!")
    @Min(value = 1, message = "Discount value must be greater than 0")
    @Max(value = 100, message = "Discount value cannot exceed 100%")
    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    // 4. No start date & 8. Start date in the past
    @NotNull(message = "Please select a start date!")
    @FutureOrPresent(message = "Start date cannot be in the past!")
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    // 5. No end date & 9. End date in the past
    @NotNull(message = "Please select an end date!")
    @FutureOrPresent(message = "End date cannot be in the past!")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 1. No-argument constructor (Required)
    public Coupon() {
    }

    // 2. All-arguments constructor
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

    // 3. GETTERS & SETTERS
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