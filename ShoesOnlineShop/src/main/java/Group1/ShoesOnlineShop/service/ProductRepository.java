package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}