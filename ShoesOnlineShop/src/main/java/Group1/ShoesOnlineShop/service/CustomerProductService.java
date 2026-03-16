package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getActiveProducts(String keyword, String category, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);

        if (keyword != null && !keyword.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
            return productRepository.searchActiveProductsByCategory(keyword.trim(), category.trim(), paging);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            return productRepository.searchActiveProducts(keyword.trim(), paging);
        } else if (category != null && !category.trim().isEmpty()) {
            return productRepository.findByCategoryNameAndIsActiveTrue(category.trim(), paging);
        } else {
            return productRepository.findByIsActiveTrue(paging);
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).filter(Product::getIsActive).orElse(null);
    }
    
    public List<Product> getFeaturedProducts() {
        return productRepository.findTop8ByIsActiveTrueOrderByIdDesc();
    }
}
