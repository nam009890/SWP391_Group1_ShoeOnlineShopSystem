package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.repository.ContentRepository;
import Group1.ShoesOnlineShop.service.ContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    // --- Khối 1: Tìm kiếm & Lấy rữ liệu ---
    @Test
    void testGetContents() {
        Pageable paging = PageRequest.of(0, 5);
        Page<Content> expectedPage = new PageImpl<>(Arrays.asList(new Content()));
        when(contentRepository.findAll(any(Specification.class), eq(paging))).thenReturn(expectedPage);

        Page<Content> result = contentService.getContents("News", "Blog", 1, 5);
        assertEquals(1, result.getContent().size());
        verify(contentRepository, times(1)).findAll(any(Specification.class), eq(paging));
    }

    @Test
    void testGetContentById_Found() {
        Content content = new Content();
        content.setId(10L);
        when(contentRepository.findById(10L)).thenReturn(Optional.of(content));

        Content result = contentService.getContentById(10L);
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void testGetContentById_NotFound() {
        when(contentRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(contentService.getContentById(99L));
    }

    // --- Khối 2: Kiểm duyệt Validation ---
    @Test
    void testValidateContent_Success() {
        Content content = new Content();
        content.setId(1L); // Update, no image required
        content.setContentText("Valid HTML Body");
        content.setContentTitle("Valid Title");

        Map<String, String> errors = contentService.validateContent(content, null);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateContent_MissingImageOnCreate() {
        Content content = new Content();
        content.setId(null); // Create new
        content.setContentText("Text");
        
        Map<String, String> errors = contentService.validateContent(content, null);
        assertTrue(errors.containsKey("imageUrl"));
        assertEquals("Please upload a thumbnail image!", errors.get("imageUrl"));
    }

    @Test
    void testValidateContent_EmptyBody() {
        Content content = new Content();
        content.setId(1L);
        content.setContentText("<p>&nbsp;</p>"); // Simulating Summernote empty body

        Map<String, String> errors = contentService.validateContent(content, null);
        assertTrue(errors.containsKey("contentText"));
        assertEquals("Content body cannot be empty!", errors.get("contentText"));
    }

    @Test
    void testValidateContent_DuplicateTitle() {
        Content content = new Content();
        content.setId(1L);
        content.setContentText("Valid text");
        content.setContentTitle("Duplicate News");

        when(contentRepository.existsByContentTitleAndIdNot("Duplicate News", 1L)).thenReturn(true);

        Map<String, String> errors = contentService.validateContent(content, null);
        assertTrue(errors.containsKey("contentTitle"));
        assertEquals("This content title already exists, please choose another!", errors.get("contentTitle"));
    }

    // --- Khối 3: Thao tác DB (Lưu/Xoá) ---
    @Test
    void testSaveContent() {
        Content content = new Content();
        contentService.saveContent(content);
        verify(contentRepository, times(1)).save(content);
        assertNotNull(content.getCreatedAt());
        assertNotNull(content.getUpdatedAt());
    }

    @Test
    void testDeleteContent() {
        contentService.deleteContent(5L);
        verify(contentRepository, times(1)).deleteById(5L);
    }
}
