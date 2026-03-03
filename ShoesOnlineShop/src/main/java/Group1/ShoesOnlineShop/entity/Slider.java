package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Group1.ShoesOnlineShop.entity.Coupon; 
import Group1.ShoesOnlineShop.entity.Product; 

@Entity
@Table(name = "sliders")
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slider_id")
    private Long id;

    // 1. Validate: Blank Slider Title
    @NotBlank(message = "Slider Title cannot be blank!")
    @Column(name = "slider_title", length = 200)
    private String sliderTitle;

    // 2. Validate: Blank image + Invalid image format
    @NotBlank(message = "Image URL cannot be blank!")
    @Pattern(regexp = "^(?i).*\\.(jpg|jpeg|png|gif|webp)(\\?.*)?$|^https?://.*", 
             message = "Invalid image format (Must be an http/https web link or end with .jpg, .png, .jpeg, .webp)")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @NotBlank(message = "Link URL cannot be blank!")
    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @NotNull(message = "Position is required!")
    @Column(name = "position")
    private Integer position = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ========================================================
    // KEEP ONLY ONE SET HERE
    // ========================================================
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

    // Constructor
    public Slider() {}

    // GETTERS & SETTERS
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

    // Getters/Setters for Lists
    public List<Coupon> getCoupons() { return coupons; }
    public void setCoupons(List<Coupon> coupons) { this.coupons = coupons; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}