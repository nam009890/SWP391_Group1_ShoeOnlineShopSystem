package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "slider_products")
public class SliderProduct {

    @EmbeddedId
    private SliderProductId id = new SliderProductId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sliderId")
    @JoinColumn(name = "slider_id")
    private Slider slider;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "discount")
    private Integer discount = 0;

    public SliderProduct() {}

    public SliderProduct(Slider slider, Product product, Integer discount) {
        this.slider = slider;
        this.product = product;
        this.discount = discount;
        if (slider != null && product != null) {
            this.id = new SliderProductId(slider.getId(), product.getProductId());
        }
    }

    // GETTERS & SETTERS
    public SliderProductId getId() { return id; }
    public void setId(SliderProductId id) { this.id = id; }

    public Slider getSlider() { return slider; }
    public void setSlider(Slider slider) { this.slider = slider; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getDiscount() { return discount; }
    public void setDiscount(Integer discount) { this.discount = discount; }

    public java.math.BigDecimal getDiscountedPrice() {
        if (product != null && product.getPrice() != null && discount != null && discount > 0) {
            return product.getPrice()
                .multiply(java.math.BigDecimal.valueOf(100 - discount))
                .divide(java.math.BigDecimal.valueOf(100), 0, java.math.RoundingMode.HALF_UP);
        }
        return product != null ? product.getPrice() : java.math.BigDecimal.ZERO;
    }
}
