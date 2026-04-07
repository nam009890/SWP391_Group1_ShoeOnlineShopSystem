package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    // ================== RELATION ==================

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // shipper (user có role SHIPPER)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User shipper;

    // ================== FIELDS ==================

    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;

    @Column(name = "delivery_status")
    private String deliveryStatus = "PENDING";

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "note")
    private String note;
    
    @Column(name = "proof_image_url")
private String proofImageUrl;
    
    @Column(name = "is_deleted")
private Boolean isDeleted = false;

    // ================== CONSTRUCTOR ==================

    public Delivery() {
        this.createdAt = LocalDateTime.now();
    }

    // ================== GETTER SETTER ==================

    public Long getDeliveryId() {
        return deliveryId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getShipper() {
        return shipper;
    }

    public void setShipper(User shipper) {
        this.shipper = shipper;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }

    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public String getProofImageUrl() {
    return proofImageUrl;
}

public void setProofImageUrl(String proofImageUrl) {
    this.proofImageUrl = proofImageUrl;
}

    public Boolean getIsDeleted() {
    return isDeleted;
}

public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
}

}