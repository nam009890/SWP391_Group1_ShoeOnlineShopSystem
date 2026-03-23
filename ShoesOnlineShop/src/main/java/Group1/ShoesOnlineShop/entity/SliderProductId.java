package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SliderProductId implements Serializable {

    @Column(name = "slider_id")
    private Long sliderId;

    @Column(name = "product_id")
    private Long productId;

    public SliderProductId() {}

    public SliderProductId(Long sliderId, Long productId) {
        this.sliderId = sliderId;
        this.productId = productId;
    }

    public Long getSliderId() {
        return sliderId;
    }

    public void setSliderId(Long sliderId) {
        this.sliderId = sliderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SliderProductId that = (SliderProductId) o;
        return Objects.equals(sliderId, that.sliderId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sliderId, productId);
    }
}
