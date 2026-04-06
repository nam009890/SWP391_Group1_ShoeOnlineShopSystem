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
    @Column(name = "discount_value", nullable = false)
    private Integer discountValue;

    @Column(name = "discount_type", length = 20)
    private String discountType = "PERCENTAGE";

    @Column(name = "max_discount_amount")
    private Integer maxDiscountAmount;

    @Column(name = "min_order_value")
    private Integer minOrderValue;

    @Column(name = "scope", length = 30)
    private String scope = "ALL";

    @Column(name = "approval_status", length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "remake_note", columnDefinition = "NVARCHAR(MAX)")
    private String remakeNote;

    @Column(name = "update_note", columnDefinition = "NVARCHAR(MAX)")
    private String updateNote;

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

    @ManyToMany
    @JoinTable(
        name = "coupon_products",
        joinColumns = @JoinColumn(name = "coupon_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private java.util.List<Product> products = new java.util.ArrayList<>();

    @Column(name = "quantity")
    private Integer quantity; // NULL = unlimited

    @Column(name = "used_count")
    private Integer usedCount = 0;

    // 1. No-argument constructor (Required)
    public Coupon() {
    }

    // 2. All-arguments constructor
    public Coupon(Long id, String couponName, String couponCode, Integer discountValue, String discountType, Integer maxDiscountAmount, Integer minOrderValue, String scope, String approvalStatus, String remakeNote, LocalDate createDate, LocalDate endDate, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.couponName = couponName;
        this.couponCode = couponCode;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderValue = minOrderValue;
        this.scope = scope;
        this.approvalStatus = approvalStatus;
        this.remakeNote = remakeNote;
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

    public Integer getDiscountValue() { return discountValue; }
    public void setDiscountValue(Integer discountValue) { this.discountValue = discountValue; }
    
    // Fallback for Thymeleaf templates that haven't been updated yet
    public Integer getDiscountPercent() { return discountValue; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public Integer getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(Integer maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public Integer getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(Integer minOrderValue) { this.minOrderValue = minOrderValue; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getRemakeNote() { return remakeNote; }
    public void setRemakeNote(String remakeNote) { this.remakeNote = remakeNote; }

    public String getUpdateNote() { return updateNote; }
    public void setUpdateNote(String updateNote) { this.updateNote = updateNote; }

    public java.util.List<Product> getProducts() { return products; }
    public void setProducts(java.util.List<Product> products) { this.products = products; }

    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getUsedCount() { return usedCount == null ? 0 : usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

}