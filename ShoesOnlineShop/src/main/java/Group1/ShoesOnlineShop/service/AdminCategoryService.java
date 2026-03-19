package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Category;
import Group1.ShoesOnlineShop.repository.CategoryRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    // === GET LIST WITH FILTER & PAGINATION ===
    public Page<Category> getCategories(String keyword, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Order.asc("name")));

        Specification<Category> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), like));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return categoryRepository.findAll(spec, pageable);
    }

    // === GET BY ID ===
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // === SAVE (Create & Update) ===
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    // === DELETE ===
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // === GET ALL ROOTS (for 2-level hierarchy) ===
    public List<Category> getRootCategories(Category excludeCategory) {
        List<Category> roots = categoryRepository.findByParentIsNullOrderByNameAsc();
        if (excludeCategory != null) {
            roots.removeIf(c -> c.getId().equals(excludeCategory.getId()));
        }
        return roots;
    }

    // === GET HIERARCHICAL FOR LISTING ===
    public List<Category> getHierarchicalCategoriesForList() {
        List<Category> roots = categoryRepository.findByParentIsNullOrderByNameAsc();
        List<Category> result = new ArrayList<>();
        for (Category root : roots) {
            result.add(root);
            if (root.getChildren() != null) {
                List<Category> children = new ArrayList<>(root.getChildren());
                children.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
                result.addAll(children);
            }
        }
        return result;
    }

    // === GET ALL ACTIVE (for dropdown) ===
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc();
    }


    // === COUNTS ===
    public long countAllCategories() {
        return categoryRepository.countAllCategories();
    }

    public long countActiveCategories() {
        return categoryRepository.countActiveCategories();
    }

    // === VALIDATION ===
    public Map<String, String> validateCategory(Category category) {
        Map<String, String> errors = new HashMap<>();

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            errors.put("name", "Tên danh mục không được để trống!");
        } else {
            boolean nameDuplicate = (category.getId() == null)
                    ? categoryRepository.existsByName(category.getName().trim())
                    : categoryRepository.existsByNameAndIdNot(category.getName().trim(), category.getId());
            if (nameDuplicate) {
                errors.put("name", "Tên danh mục này đã tồn tại!");
            }
        }

        if (category.getParent() != null && category.getId() != null) {
            if (category.getParent().getId().equals(category.getId())) {
                errors.put("parent", "Danh mục không thể là cha của chính nó!");
            }
        }

        return errors;
    }
}
