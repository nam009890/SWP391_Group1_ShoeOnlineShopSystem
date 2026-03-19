package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/contents")
    public String listContents(
            Model model,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "type", required = false) String type, // Nhận thêm filter type
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        Page<Content> pageContents = contentService.getContents(keyword, type, page, size);
        model.addAttribute("contents", pageContents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageContents.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type); // Gửi type lại view để giữ trạng thái
        return "content-list"; 
    }

    @GetMapping("/contents/create")
    public String showCreateContentForm(Model model) {
        model.addAttribute("content", new Content());
        return "content-create";
    }

    // Xử lý tạo/sửa kèm theo file ảnh Thumbnail
    @PostMapping("/contents/save")
    public String saveContent(
            @Valid @ModelAttribute("content") Content content, 
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
            Model model) {

        if (result.hasErrors()) {
            return content.getId() == null ? "content-create" : "content-update";
        }

        try {
            // Xử lý upload file ảnh (nếu người dùng có chọn file)
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + org.springframework.util.StringUtils.cleanPath(imageFile.getOriginalFilename());
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("src/main/resources/static/uploads/contents/");
                
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                
                try (java.io.InputStream inputStream = imageFile.getInputStream()) {
                    java.nio.file.Files.copy(inputStream, uploadPath.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    // Gắn đường dẫn nội bộ vào trường imageUrl
                    content.setImageUrl("/uploads/contents/" + fileName); 
                }
            } else if (content.getId() != null) {
                // Nếu đang Update mà không chọn ảnh mới -> Giữ nguyên ảnh cũ trong DB
                Content existingContent = contentService.getContentById(content.getId());
                if (existingContent != null) {
                    content.setImageUrl(existingContent.getImageUrl());
                }
            }
            
            contentService.saveContent(content);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
            return content.getId() == null ? "content-create" : "content-update";
        }

        return "redirect:/contents";
    }

    @GetMapping("/contents/update/{id}")
    public String showUpdateContentForm(@PathVariable(name = "id") Long id, Model model) {
        Content content = contentService.getContentById(id);
        if (content == null) return "redirect:/contents";
        model.addAttribute("content", content);
        return "content-update";
    }

    @GetMapping("/contents/delete/{id}")
    public String deleteContent(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        contentService.deleteContent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Content deleted successfully!");
        return "redirect:/contents";
    }

    @GetMapping("/contents/detail/{id}")
    public String showContentDetail(@PathVariable(name = "id") Long id, Model model) {
        Content content = contentService.getContentById(id);
        if (content == null) return "redirect:/contents";
        model.addAttribute("content", content);
        return "content-detail"; 
    }

    // ========================================================
    // API NGẦM CHO SUMMERNOTE (CHÈN ẢNH VÀO GIỮA BÀI VIẾT)
    // ========================================================
    @PostMapping("/contents/upload-image")
    @ResponseBody // Trả về String thẳng cho trình duyệt, không trả về HTML
    public ResponseEntity<String> uploadInlineImage(@RequestParam(name = "file") MultipartFile file) {
        try {
            String imageUrl = contentService.saveImageFile(file);
            return ResponseEntity.ok(imageUrl); // Trả link ảnh về cho Summernote
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed");
        }
    }
}