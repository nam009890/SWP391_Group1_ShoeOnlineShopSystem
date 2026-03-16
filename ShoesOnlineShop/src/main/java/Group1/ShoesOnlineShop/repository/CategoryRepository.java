package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = true")
    long countActiveCategories();

    @Query("SELECT COUNT(c) FROM Category c")
    long countAllCategories();

    List<Category> findByIsActiveTrueOrderByDisplayOrderAscNameAsc();

    List<Category> findByParentIsNullOrderByDisplayOrderAscNameAsc();
}
