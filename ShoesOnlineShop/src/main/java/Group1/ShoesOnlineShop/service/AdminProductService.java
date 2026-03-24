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

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/products/";

    // === Regex Constants ===
    private static final String PRODUCT_NAME_REGEX = "^[\\p{L}0-9\\s\\-]+$";
    private static final String SIZE_REGEX = "^\\d+(,\\s*\\d+)*$";
    private static final String COLOR_REGEX = "^[\\p{L}\\s,\\-/]+$";

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

        Path uploadPath = Paths.get(UPLOAD_DIR);
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

        // --- Product Name ---
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.put("name", "Product name cannot be blank!");
        } else {
            String trimmedName = product.getName().trim();
            if (trimmedName.length() < 2 || trimmedName.length() > 200) {
                errors.put("name", "Product name must be between 2 and 200 characters!");
            } else if (!trimmedName.matches(PRODUCT_NAME_REGEX)) {
                errors.put("name", "Product name can only contain letters, numbers, spaces, and hyphens!");
            } else {
                boolean nameDuplicate = (product.getId() == null)
                        ? adminProductRepository.existsByName(trimmedName)
                        : adminProductRepository.existsByNameAndIdNot(trimmedName, product.getId());
                if (nameDuplicate) {
                    errors.put("name", "This product name already exists!");
                }
            }
        }

        // --- Price ---
        if (product.getPrice() == null) {
            errors.put("price", "Price cannot be blank!");
        } else if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Price must be greater than 0!");
        } else if (product.getPrice().compareTo(new BigDecimal("999999999")) > 0) {
            errors.put("price", "Price cannot exceed 999,999,999!");
        }

        // --- Stock Quantity ---
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            errors.put("stockQuantity", "Stock quantity must be 0 or greater!");
        } else if (product.getStockQuantity() > 99999) {
            errors.put("stockQuantity", "Stock quantity cannot exceed 99,999!");
        }

        // --- Size ---
        if (product.getSize() != null && !product.getSize().trim().isEmpty()) {
            String trimmedSize = product.getSize().trim();
            if (!trimmedSize.matches(SIZE_REGEX)) {
                errors.put("size", "Size must be numeric values (e.g., 38 or 38, 39, 40)!");
            } else {
                // Validate each size value is within valid range (15-50)
                String[] sizeValues = trimmedSize.split(",");
                for (String sizeVal : sizeValues) {
                    try {
                        int sizeNum = Integer.parseInt(sizeVal.trim());
                        if (sizeNum < 15 || sizeNum > 50) {
                            errors.put("size", "Each size value must be between 15 and 50!");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        errors.put("size", "Size must contain only valid numbers!");
                        break;
                    }
                }
            }
        }

        // --- Color ---
        if (product.getColor() != null && !product.getColor().trim().isEmpty()) {
            String trimmedColor = product.getColor().trim();
            if (trimmedColor.length() > 100) {
                errors.put("color", "Color must not exceed 100 characters!");
            } else if (!trimmedColor.matches(COLOR_REGEX)) {
                errors.put("color", "Color can only contain letters, spaces, commas, slashes, and hyphens!");
            }
        }

        // --- Description ---
        if (product.getProductDescription() != null && product.getProductDescription().trim().length() > 500) {
            errors.put("productDescription", "Description must not exceed 500 characters!");
        }

        return errors;
    }
}
