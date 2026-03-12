package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByName(String name);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p")
    long countAllProducts();
}
