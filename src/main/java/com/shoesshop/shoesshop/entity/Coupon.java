package com.shoesshop.shoesshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Import thư viện Validate
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_name", nullable = false)
    @NotBlank(message = "Please enter coupon name") // Không được để trống
    private String name;

    @Column(name = "coupon_code", unique = true, nullable = false)
    @NotBlank(message = "Please enter coupon code")
    @Size(min = 3, max = 20, message = "Code must be between 3 and 20 characters")
    private String code;

    @NotNull(message = "Please select discount")
    @Min(value = 5, message = "Minimum discount is 5%")
    @Max(value = 50, message = "Maximum discount is 50%")
    private Integer discountPercent;

    @NotNull(message = "Start date is required")
    private LocalDate createDate; // Trong form gọi là Start Date
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future") // Ngày kết thúc phải ở tương lai
    private LocalDate endDate;
    
    private Boolean active = true;

    // Constructor rỗng
    public Coupon() {}

    // Constructor đầy đủ
    public Coupon(Long id, String name, String code, Integer discountPercent, LocalDate createDate, LocalDate endDate, Boolean active) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.discountPercent = discountPercent;
        this.createDate = createDate;
        this.endDate = endDate;
        this.active = active;
    }

    // --- GETTER & SETTER (Giữ nguyên như cũ, tôi viết gọn lại để bạn dễ nhìn) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}