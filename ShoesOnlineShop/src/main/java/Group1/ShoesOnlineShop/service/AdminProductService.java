package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.repository.AdminProductRepository;
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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminProductService {

    @Autowired
    private AdminProductRepository adminProductRepository;

    // Removed local UPLOAD_DIR in favor of WebMvcConfig.UPLOAD_DIR

    // === GET LIST WITH FILTER & PAGINATION ===
    public Page<Product> getProducts(String keyword, Long categoryId, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), like));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.join("category").get("id"), categoryId));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return adminProductRepository.findAll(spec, pageable);
    }

    // === GET BY ID ===
    public Product getProductById(Long id) {
        return adminProductRepository.findById(id).orElse(null);
    }

    // === SAVE (Create & Update) ===
    public void saveProduct(Product product) {
        adminProductRepository.save(product);
    }

    // === DELETE ===
    public void deleteProduct(Long id) {
        adminProductRepository.deleteById(id);
    }

    // === UPLOAD IMAGE ===
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép upload file ảnh!");
        }

        // Validate image dimensions
        try (java.io.InputStream is = file.getInputStream()) {
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(is);
            if (image == null) {
                throw new IllegalArgumentException("File không hợp lệ hoặc bị lỗi định dạng ảnh!");
            }
            int width = image.getWidth();
            int height = image.getHeight();
            if (width > 2560 || height > 2560) {
                throw new IllegalArgumentException("Kích thước ảnh không được vượt quá độ phân giải 2K (2560 pixels).");
            }
        }

        Path uploadPath = Group1.ShoesOnlineShop.config.WebMvcConfig.UPLOAD_DIR.resolve("products");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/products/" + fileName;
    }


    // === COUNTS ===
    public long countAllProducts() {
        return adminProductRepository.countAllProducts();
    }

    public long countActiveProducts() {
        return adminProductRepository.countActiveProducts();
    }

    // === VALIDATION ===
    public Map<String, String> validateProduct(Product product) {
        Map<String, String> errors = new HashMap<>();

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.put("name", "Product name cannot be blank!");
        } else {
            boolean nameDuplicate = (product.getId() == null)
                    ? adminProductRepository.existsByName(product.getName().trim())
                    : adminProductRepository.existsByNameAndIdNot(product.getName().trim(), product.getId());
            if (nameDuplicate) {
                errors.put("name", "This product name already exists!");
            }
        }

        if (product.getPrice() == null) {
            errors.put("price", "Price cannot be blank!");
        } else if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Price must be greater than 0!");
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            errors.put("stockQuantity", "Stock quantity must be 0 or greater!");
        }

        return errors;
    }
}
