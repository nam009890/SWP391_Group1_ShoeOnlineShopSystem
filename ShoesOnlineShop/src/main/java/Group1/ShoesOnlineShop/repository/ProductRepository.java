package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "LOWER(p.categoryName) = LOWER(:category)")
    Page<Product> findByCategoryNameAndIsActiveTrue(@Param("category") String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "LOWER(p.categoryName) = LOWER(:category) AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchActiveProductsByCategory(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);
    
    List<Product> findTop8ByIsActiveTrueOrderByIdDesc();

    List<Product> findByCategoryNameAndIsActiveTrue(String categoryName);

    List<Product> findByIsActiveTrueOrderByIdDesc();

    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}