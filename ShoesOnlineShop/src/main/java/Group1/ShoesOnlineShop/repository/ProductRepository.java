package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Featured products for home page
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();

    // Filter by price range
    List<Product> findByIsActiveTrueAndPriceBetweenOrderByCreatedAtDesc(BigDecimal minPrice, BigDecimal maxPrice);

    // Filter by category IDs (supports parent + child)
    List<Product> findByIsActiveTrueAndCategory_IdInOrderByCreatedAtDesc(List<Long> categoryIds);

    // Filter by category IDs and price range
    List<Product> findByIsActiveTrueAndCategory_IdInAndPriceBetweenOrderByCreatedAtDesc(
            List<Long> categoryIds, BigDecimal minPrice, BigDecimal maxPrice);
}