package com.shoesshop.shoesshop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.shoesshop.shoesshop.entity.Product;

@Entity
@Table(name = "sliders")
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slider_id")
    private Long id;

    @Column(name = "slider_title", length = 200)
    private String sliderTitle;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(name = "position")
    private Integer position = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ========================================================
    // ĐÂY CHÍNH LÀ BIẾN BỊ THIẾU GÂY LỖI:
    // Dạy Spring Boot cách nối sang bảng Coupons
    // ========================================================
    @ManyToMany
    @JoinTable(
        name = "slider_coupons",
        joinColumns = @JoinColumn(name = "slider_id"),
        inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private List<Coupon> coupons = new ArrayList<>();


    // 1. Constructor không tham số (Bắt buộc)
    public Slider() {
    }

    // 2. Constructor có tham số
    public Slider(Long id, String sliderTitle, String imageUrl, String linkUrl, Integer position, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sliderTitle = sliderTitle;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.position = position;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========================================================
    // 3. GETTER & SETTER (Cho tất cả các biến)
    // ========================================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSliderTitle() { return sliderTitle; }
    public void setSliderTitle(String sliderTitle) { this.sliderTitle = sliderTitle; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // GETTER & SETTER cho danh sách Coupon
    public List<Coupon> getCoupons() { return coupons; }
    public void setCoupons(List<Coupon> coupons) { this.coupons = coupons; }
    // 1. Nối với bảng Coupons
    @ManyToMany
    @JoinTable(
        name = "slider_coupons",
        joinColumns = @JoinColumn(name = "slider_id"),
        inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private java.util.List<Coupon> coupons = new java.util.ArrayList<>();

    // 2. Nối với bảng Products
    @ManyToMany
    @JoinTable(
        name = "slider_products",
        joinColumns = @JoinColumn(name = "slider_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private java.util.List<Product> products = new java.util.ArrayList<>();


    // --- NHỚ THÊM GETTER & SETTER CHO 2 BIẾN NÀY ---
    public java.util.List<Coupon> getCoupons() { return coupons; }
    public void setCoupons(java.util.List<Coupon> coupons) { this.coupons = coupons; }
}