package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProductId(Long productId);
}
