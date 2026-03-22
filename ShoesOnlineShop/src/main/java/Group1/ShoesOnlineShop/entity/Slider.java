package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sliders")
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slider_id")
    private Long id;

    @NotBlank(message = "Slider Title cannot be blank!")
    @Column(name = "slider_title", length = 200)
    private String sliderTitle;

    // Đã bỏ @NotBlank và @Pattern để hỗ trợ upload file cục bộ mượt mà hơn
    @Column(name = "image_url", length = 500)
    private String imageUrl;



    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "position")
    private Integer position = 0;

    @ManyToMany
    @JoinTable(
        name = "slider_coupons",
        joinColumns = @JoinColumn(name = "slider_id"),
        inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private List<Coupon> coupons = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "slider_products",
        joinColumns = @JoinColumn(name = "slider_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    public Slider() {}

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSliderTitle() { return sliderTitle; }
    public void setSliderTitle(String sliderTitle) { this.sliderTitle = sliderTitle; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
    public List<Coupon> getCoupons() { return coupons; }
    public void setCoupons(List<Coupon> coupons) { this.coupons = coupons; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}