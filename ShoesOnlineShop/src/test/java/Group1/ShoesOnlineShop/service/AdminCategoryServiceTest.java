package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Category;
import Group1.ShoesOnlineShop.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AdminCategoryService adminCategoryService;

    // 1. SAVE - Lưu danh mục hợp lệ
    @Test
    void testSaveCategory_Success() {
        Category category = new Category();
        category.setName("Giày Chạy Bộ");
        category.setDisplayOrder(1);
        category.setIsActive(true);

        adminCategoryService.saveCategory(category);

        verify(categoryRepository, times(1)).save(category);
    }

    // 2. VALIDATION - Tên danh mục bị để trống
    @Test
    void testValidateCategory_BlankName() {
        Category category = new Category();
        category.setName("   ");
        category.setDisplayOrder(0);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("name"));
        assertEquals("Category name must not be empty!", errors.get("name"));
    }

    // 3. VALIDATION - Tên null
    @Test
    void testValidateCategory_NullName() {
        Category category = new Category();
        category.setName(null);
        category.setDisplayOrder(0);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.containsKey("name"));
        assertEquals("Category name must not be empty!", errors.get("name"));
    }

    // 4. VALIDATION - Tên quá ngắn
    @Test
    void testValidateCategory_NameTooShort() {
        Category category = new Category();
        category.setName("A");
        category.setDisplayOrder(0);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.containsKey("name"));
        assertEquals("Category name must be between 2 and 100 characters!", errors.get("name"));
    }

    // 5. VALIDATION - Tên chứa ký tự đặc biệt không hợp lệ
    @Test
    void testValidateCategory_NameWithSpecialChars() {
        Category category = new Category();
        category.setName("Shoes @#$!");
        category.setDisplayOrder(0);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.containsKey("name"));
        assertEquals("Category name can only contain letters, numbers, spaces, hyphens, and '&'!", errors.get("name"));
    }

    // 6. VALIDATION - Tên danh mục đã tồn tại (tạo mới)
    @Test
    void testValidateCategory_DuplicateName_CreateNew() {
        Category category = new Category();
        category.setName("Running Shoes");
        category.setDisplayOrder(2);

        when(categoryRepository.existsByName("Running Shoes")).thenReturn(true);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.containsKey("name"));
        assertEquals("This category name already exists!", errors.get("name"));
    }

    // 7. VALIDATION - Tên đã tồn tại nhưng khi UPDATE chính nó (không lỗi)
    @Test
    void testValidateCategory_DuplicateName_UpdateSelf_ShouldPass() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Giày Thể Thao");
        category.setDisplayOrder(0);

        when(categoryRepository.existsByNameAndIdNot("Giày Thể Thao", 1L)).thenReturn(false);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.isEmpty());
    }

    // 8. VALIDATION - Danh mục hợp lệ hoàn toàn (không có lỗi)
    @Test
    void testValidateCategory_ValidCategory_NoErrors() {
        Category category = new Category();
        category.setName("Quần Ngắn");
        category.setDisplayOrder(3);

        when(categoryRepository.existsByName("Quần Ngắn")).thenReturn(false);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.isEmpty());
    }

    // 9. VALIDATION - displayOrder âm → phải có lỗi
    @Test
    void testValidateCategory_NegativeDisplayOrder() {
        Category category = new Category();
        category.setName("Ba Lô");
        category.setDisplayOrder(-1);

        when(categoryRepository.existsByName("Ba Lô")).thenReturn(false);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("displayOrder"));
    }

    // 10. VALIDATION - Tên hợp lệ với ký tự & và gạch ngang
    @Test
    void testValidateCategory_NameWithAmpersandAndHyphen() {
        Category category = new Category();
        category.setName("Shoes & Boots - Sale");
        category.setDisplayOrder(0);

        when(categoryRepository.existsByName("Shoes & Boots - Sale")).thenReturn(false);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertFalse(errors.containsKey("name"));
    }

    // 11. DELETE - Xóa danh mục
    @Test
    void testDeleteCategory_Success() {
        Long categoryId = 1L;
        adminCategoryService.deleteCategory(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    // 12. GET BY ID - Không tìm thấy
    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        Category result = adminCategoryService.getCategoryById(999L);
        assertNull(result);
    }

    // 13. GET BY ID - Tìm thấy
    @Test
    void testGetCategoryById_Found() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Giày Luyện Tập");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = adminCategoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Giày Luyện Tập", result.getName());
        assertEquals(1L, result.getId());
    }

    // 14. COUNT - Đếm tổng số danh mục
    @Test
    void testCountAllCategories() {
        when(categoryRepository.countAllCategories()).thenReturn(10L);
        long count = adminCategoryService.countAllCategories();
        assertEquals(10L, count);
    }

    // 15. COUNT - Đếm danh mục đang hoạt động
    @Test
    void testCountActiveCategories() {
        when(categoryRepository.countActiveCategories()).thenReturn(7L);
        long count = adminCategoryService.countActiveCategories();
        assertEquals(7L, count);
    }
}
