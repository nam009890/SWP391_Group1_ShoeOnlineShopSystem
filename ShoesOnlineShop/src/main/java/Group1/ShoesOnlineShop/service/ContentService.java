package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    // 1. Hàm get có áp dụng bộ lọc (Keyword & Type)
    public Page<Content> getContents(String keyword, String type, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);

        Specification<Content> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("contentTitle")), "%" + keyword.toLowerCase() + "%"));
            }
            if (type != null && !type.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("contentType"), type));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return contentRepository.findAll(spec, paging);
    }

    public void saveContent(Content content) {
        if (content.getCreatedAt() == null) {
            content.setCreatedAt(LocalDateTime.now());
        }
        content.setUpdatedAt(LocalDateTime.now());
        contentRepository.save(content);
    }

    public Content getContentById(Long id) {
        return contentRepository.findById(id).orElse(null);
    }

    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }

    public boolean isContentTitleExists(String title, Long id) {
        if (id == null) {
            return contentRepository.existsByContentTitle(title);
        }
        return contentRepository.existsByContentTitleAndIdNot(title, id);
    }

    public Map<String, String> validateContent(Content content, MultipartFile imageFile) {
        Map<String, String> errors = new HashMap<>();
        boolean isNew = (content.getId() == null);
        
        if (isNew && (imageFile == null || imageFile.isEmpty())) {
            errors.put("imageUrl", "Please upload a thumbnail image!");
        }

        if (content.getContentText() == null || content.getContentText().replaceAll("<[^>]*>", "").replaceAll("&nbsp;", "").trim().isEmpty()) {
            errors.put("contentText", "Content body cannot be empty!");
        }

        if (content.getContentTitle() != null && !content.getContentTitle().trim().isEmpty()
                && isContentTitleExists(content.getContentTitle().trim(), content.getId())) {
            errors.put("contentTitle", "This content title already exists, please choose another!");
        }
        
        return errors;
    }

    // 2. HÀM XỬ LÝ LƯU FILE ẢNH VÀO Ổ CỨNG MÁY TÍNH
    public String saveImageFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        Path uploadPath = Group1.ShoesOnlineShop.config.WebMvcConfig.UPLOAD_DIR.resolve("contents");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/contents/" + fileName; // Trả về đường dẫn để lưu vào DB
        }
    }
}