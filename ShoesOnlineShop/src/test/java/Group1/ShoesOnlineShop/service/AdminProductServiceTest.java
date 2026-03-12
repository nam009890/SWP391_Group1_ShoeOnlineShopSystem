package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Product;
import Group1.ShoesOnlineShop.repository.AdminProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

    @Mock
    private AdminProductRepository adminProductRepository;

    @InjectMocks
    private AdminProductService adminProductService;

    // 1. SUCCESSFUL - Lưu sản phẩm hợp lệ
    @Test
    void testSaveProduct_Success() {
        Product product = new Product();
        product.setName("Nike Air Max");
        product.setPrice(new BigDecimal("2500000"));
        product.setStockQuantity(10);

        adminProductService.saveProduct(product);

        verify(adminProductRepository, times(1)).save(product);
    }

    // 2. VALIDATION - Tên sản phẩm bị để trống
    @Test
    void testValidateProduct_BlankName() {
        Product product = new Product();
        product.setName("");
        product.setPrice(new BigDecimal("100000"));
        product.setStockQuantity(5);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("name"));
        assertEquals("Product name cannot be blank!", errors.get("name"));
    }

    // 3. VALIDATION - Tên null
    @Test
    void testValidateProduct_NullName() {
        Product product = new Product();
        product.setName(null);
        product.setPrice(new BigDecimal("100000"));
        product.setStockQuantity(5);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertTrue(errors.containsKey("name"));
    }

    // 4. VALIDATION - Giá sản phẩm <= 0
    @Test
    void testValidateProduct_NegativePrice() {
        Product product = new Product();
        product.setName("Test Shoe");
        product.setPrice(new BigDecimal("-100"));
        product.setStockQuantity(5);

        when(adminProductRepository.existsByName("Test Shoe")).thenReturn(false);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("price"));
        assertEquals("Price must be greater than 0!", errors.get("price"));
    }

    // 5. VALIDATION - Giá bằng 0
    @Test
    void testValidateProduct_ZeroPrice() {
        Product product = new Product();
        product.setName("Test Shoe");
        product.setPrice(BigDecimal.ZERO);
        product.setStockQuantity(5);

        when(adminProductRepository.existsByName("Test Shoe")).thenReturn(false);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertTrue(errors.containsKey("price"));
    }

    // 6. VALIDATION - Giá là null
    @Test
    void testValidateProduct_NullPrice() {
        Product product = new Product();
        product.setName("Test Shoe");
        product.setPrice(null);
        product.setStockQuantity(5);

        when(adminProductRepository.existsByName("Test Shoe")).thenReturn(false);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertTrue(errors.containsKey("price"));
        assertEquals("Price cannot be blank!", errors.get("price"));
    }

    // 7. VALIDATION - Số lượng tồn kho âm
    @Test
    void testValidateProduct_NegativeStock() {
        Product product = new Product();
        product.setName("Test Shoe");
        product.setPrice(new BigDecimal("1000000"));
        product.setStockQuantity(-5);

        when(adminProductRepository.existsByName("Test Shoe")).thenReturn(false);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertTrue(errors.containsKey("stockQuantity"));
        assertEquals("Stock quantity must be 0 or greater!", errors.get("stockQuantity"));
    }

    // 8. VALIDATION - Tên sản phẩm đã tồn tại (tạo mới)
    @Test
    void testValidateProduct_DuplicateName_CreateNew() {
        Product product = new Product();
        product.setName("Nike Air Force");
        product.setPrice(new BigDecimal("1500000"));
        product.setStockQuantity(5);

        when(adminProductRepository.existsByName("Nike Air Force")).thenReturn(true);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertTrue(errors.containsKey("name"));
        assertEquals("This product name already exists!", errors.get("name"));
    }

    // 9. VALIDATION - Sản phẩm hợp lệ hoàn toàn (không có lỗi)
    @Test
    void testValidateProduct_ValidProduct_NoErrors() {
        Product product = new Product();
        product.setName("Adidas Ultra Boost");
        product.setPrice(new BigDecimal("3000000"));
        product.setStockQuantity(20);

        when(adminProductRepository.existsByName("Adidas Ultra Boost")).thenReturn(false);

        Map<String, String> errors = adminProductService.validateProduct(product);

        assertTrue(errors.isEmpty());
    }

    // 10. DELETE - Xóa sản phẩm
    @Test
    void testDeleteProduct_Success() {
        Long productId = 1L;
        adminProductService.deleteProduct(productId);
        verify(adminProductRepository, times(1)).deleteById(productId);
    }

    // 11. GET BY ID - Sản phẩm không tìm thấy
    @Test
    void testGetProductById_NotFound() {
        when(adminProductRepository.findById(999L)).thenReturn(Optional.empty());
        Product result = adminProductService.getProductById(999L);
        assertNull(result);
    }

    // 12. GET BY ID - Tìm thấy sản phẩm
    @Test
    void testGetProductById_Found() {
        Product product = new Product();
        product.setName("Test");
        when(adminProductRepository.findById(1L)).thenReturn(Optional.of(product));
        Product result = adminProductService.getProductById(1L);
        assertNotNull(result);
        assertEquals("Test", result.getName());
    }
}
