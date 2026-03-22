package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort;
import java.util.List;

@Service
public class CustomerProductService {

    @Autowired
    private ProductRepository productRepository;

    private java.util.List<Product> getMockProducts() {
        java.util.List<Product> mockList = new java.util.ArrayList<>();
        
        // Sneaker category
        mockList.add(createMockProduct(1L, "Nike Air Force 1", 2500000.0, "Sneaker", "Nike", "/images/product1.jpg"));
        mockList.add(createMockProduct(2L, "Adidas Ultraboost", 3200000.0, "Running", "Adidas", "/images/product2.jpg"));
        mockList.add(createMockProduct(3L, "Puma RS-X", 2100000.0, "Sneaker", "Puma", "/images/product3.jpg"));
        
        // Jordan category
        mockList.add(createMockProduct(6L, "Nike Air Jordan 1", 4500000.0, "Basketball", "Nike", "/images/product6.jpg"));
        
        // Bitis brand
        mockList.add(createMockProduct(9L, "Bitis Hunter X", 1200000.0, "Sneaker", "Bitis", "/images/product9.jpg"));
        
        // New Arrival
        mockList.add(createMockProduct(10L, "New Balance 550", 3000000.0, "Sneaker", "New Balance", "/images/product10.jpg"));
        
        return mockList;
    }

    private Product createMockProduct(Long id, String name, Double price, String category, String brand, String imageUrl) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(java.math.BigDecimal.valueOf(price));
        p.setCategoryName(category);
        p.setIsActive(true);
        p.setImageUrl(imageUrl);
        // Note: Brand is included in name for now to match repository patterns if needed
        return p;
    }

    public Page<Product> getActiveProducts(String keyword, String category, String sort, int page, int size) {
        // Mock implementation of pagination
        java.util.List<Product> products = getAll();
        if (category != null && !category.isEmpty()) {
            products = getByCategory(category);
        }
        int start = (page - 1) * size;
        int end = Math.min(start + size, products.size());
        java.util.List<Product> pagedList = (start < products.size()) ? products.subList(start, end) : new java.util.ArrayList<>();
        
        return new org.springframework.data.domain.PageImpl<>(pagedList, org.springframework.data.domain.PageRequest.of(page - 1, size), products.size());
    }

    public Product getProductById(Long id) {
        return getMockProducts().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public java.util.List<Product> getFeaturedProducts() {
        return getMockProducts().subList(0, Math.min(8, getMockProducts().size()));
    }

    public java.util.List<Product> getByCategory(String category) {
        return getMockProducts().stream()
                .filter(p -> p.getCategoryName().equalsIgnoreCase(category))
                .collect(java.util.stream.Collectors.toList());
    }
    
    public java.util.List<Product> getByBrand(String brand) {
        return getMockProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(brand.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.List<Product> getNewest() {
        java.util.List<Product> products = getMockProducts();
        products.sort((a, b) -> b.getId().compareTo(a.getId()));
        return products;
    }

    public java.util.List<Product> getAll() {
        return getMockProducts();
    }
}
