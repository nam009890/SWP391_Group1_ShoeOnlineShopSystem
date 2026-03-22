package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {
    List<ProductColor> findByProductId(Long productId);
}
