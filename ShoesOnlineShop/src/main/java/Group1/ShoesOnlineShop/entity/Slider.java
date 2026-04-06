package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "remake_note", columnDefinition = "NVARCHAR(MAX)")
    private String remakeNote;

    @Column(name = "update_note", columnDefinition = "NVARCHAR(MAX)")
    private String updateNote;



    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
        name = "slider_coupons",
        joinColumns = @JoinColumn(name = "slider_id"),
        inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private List<Coupon> coupons = new ArrayList<>();

    @OneToMany(mappedBy = "slider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SliderProduct> sliderProducts = new ArrayList<>();

    public Slider() {}

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSliderTitle() { return sliderTitle; }
    public void setSliderTitle(String sliderTitle) { this.sliderTitle = sliderTitle; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getRemakeNote() { return remakeNote; }
    public void setRemakeNote(String remakeNote) { this.remakeNote = remakeNote; }
    public String getUpdateNote() { return updateNote; }
    public void setUpdateNote(String updateNote) { this.updateNote = updateNote; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<Coupon> getCoupons() { return coupons; }
    public void setCoupons(List<Coupon> coupons) { this.coupons = coupons; }
    public List<SliderProduct> getSliderProducts() { return sliderProducts; }
    public void setSliderProducts(List<SliderProduct> sliderProducts) { this.sliderProducts = sliderProducts; }
    
    // Convenience method to add a product with discount
    public void addProduct(Product product, Integer discount) {
        SliderProduct sliderProduct = new SliderProduct(this, product, discount);
        this.sliderProducts.add(sliderProduct);
    }
    
    // Convenience method to remove a product
    public void removeProduct(Product product) {
        this.sliderProducts.removeIf(sp -> sp.getProduct().equals(product));
    }
}