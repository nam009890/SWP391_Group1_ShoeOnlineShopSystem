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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/categories/";

    // === GET LIST WITH FILTER & PAGINATION ===
    public Page<Category> getCategories(String keyword, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("name")));

        Specification<Category> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
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

    // === GET ALL FLATTENED (for dropdowns / hierarchy list) ===
    public List<Category> getFlattenedCategories(Category excludeCategory) {
        List<Category> roots = categoryRepository.findByParentIsNullOrderByDisplayOrderAscNameAsc();
        List<Category> flattened = new ArrayList<>();
        for (Category root : roots) {
            buildFlattenedTree(root, 0, flattened, excludeCategory);
        }
        return flattened;
    }

    private void buildFlattenedTree(Category node, int level, List<Category> flattened, Category excludeCategory) {
        if (excludeCategory != null && node.getId().equals(excludeCategory.getId())) {
            return; // skip this node and all its children to prevent circular reference
        }
        
        node.setLevel(level);
        String prefix = "— ".repeat(level);
        node.setFormattedName(prefix + node.getName());
        flattened.add(node);
        
        List<Category> children = node.getChildren();
        if (children != null) {
            children.sort((c1, c2) -> {
                int orderCmp = Integer.compare(c1.getDisplayOrder(), c2.getDisplayOrder());
                if (orderCmp == 0) return c1.getName().compareToIgnoreCase(c2.getName());
                return orderCmp;
            });
            for (Category child : children) {
                buildFlattenedTree(child, level + 1, flattened, excludeCategory);
            }
        }
    }

    // === GET ALL ACTIVE (for dropdown) ===
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAscNameAsc();
    }

    // === UPLOAD IMAGE ===
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed!");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/categories/" + fileName;
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

        if (category.getDisplayOrder() != null && category.getDisplayOrder() < 0) {
            errors.put("displayOrder", "Thứ tự hiển thị phải >= 0!");
        }

        return errors;
    }
}
