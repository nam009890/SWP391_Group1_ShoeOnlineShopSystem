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

    // 4. VALIDATION - Tên danh mục đã tồn tại (tạo mới)
    @Test
    void testValidateCategory_DuplicateName_CreateNew() {
        Category category = new Category();
        category.setName("Áo Thun");
        category.setDisplayOrder(2);

        when(categoryRepository.existsByName("Áo Thun")).thenReturn(true);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.containsKey("name"));
        assertEquals("This category name already exists!", errors.get("name"));
    }

    // 5. VALIDATION - Tên đã tồn tại nhưng khi UPDATE chính nó (không lỗi)
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

    // 6. VALIDATION - Danh mục hợp lệ hoàn toàn (không có lỗi)
    @Test
    void testValidateCategory_ValidCategory_NoErrors() {
        Category category = new Category();
        category.setName("Quần Ngắn");
        category.setDisplayOrder(3);

        when(categoryRepository.existsByName("Quần Ngắn")).thenReturn(false);

        Map<String, String> errors = adminCategoryService.validateCategory(category);

        assertTrue(errors.isEmpty());
    }

    // 7. VALIDATION - displayOrder âm → phải có lỗi
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

    // 8. DELETE - Xóa danh mục
    @Test
    void testDeleteCategory_Success() {
        Long categoryId = 1L;
        adminCategoryService.deleteCategory(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    // 9. GET BY ID - Không tìm thấy
    @Test
    void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        Category result = adminCategoryService.getCategoryById(999L);
        assertNull(result);
    }

    // 10. GET BY ID - Tìm thấy
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

    // 11. COUNT - Đếm tổng số danh mục
    @Test
    void testCountAllCategories() {
        when(categoryRepository.countAllCategories()).thenReturn(10L);
        long count = adminCategoryService.countAllCategories();
        assertEquals(10L, count);
    }

    // 12. COUNT - Đếm danh mục đang hoạt động
    @Test
    void testCountActiveCategories() {
        when(categoryRepository.countActiveCategories()).thenReturn(7L);
        long count = adminCategoryService.countActiveCategories();
        assertEquals(7L, count);
    }
}
