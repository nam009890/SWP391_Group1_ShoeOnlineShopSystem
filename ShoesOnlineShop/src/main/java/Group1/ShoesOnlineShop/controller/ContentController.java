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
@RequestMapping("/internal/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping
    public String listContents(
            Model model,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        Page<Content> pageContents = contentService.getContents(keyword, type, page, size);
        model.addAttribute("contents", pageContents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageContents.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        return "content-list"; 
    }

    @GetMapping("/create")
    public String showCreateContentForm(Model model) {
        model.addAttribute("content", new Content());
        return "content-create";
    }

    @PostMapping("/save")
    public String saveContent(
            @Valid @ModelAttribute("content") Content content, 
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean isNew = (content.getId() == null);

        java.util.Map<String, String> errors = contentService.validateContent(content, imageFile);
        if (!errors.isEmpty()) {
            errors.forEach((field, message) -> result.rejectValue(field, "error.content", message));
        }

        if (result.hasErrors()) {
            return isNew ? "content-create" : "content-update";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + org.springframework.util.StringUtils.cleanPath(imageFile.getOriginalFilename());
                java.nio.file.Path uploadPath = Group1.ShoesOnlineShop.config.WebMvcConfig.UPLOAD_DIR.resolve("contents");
                
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                
                try (java.io.InputStream inputStream = imageFile.getInputStream()) {
                    java.nio.file.Files.copy(inputStream, uploadPath.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    content.setImageUrl("/uploads/contents/" + fileName); 
                }
            } else if (content.getId() != null) {
                Content existingContent = contentService.getContentById(content.getId());
                if (existingContent != null) {
                    content.setImageUrl(existingContent.getImageUrl());
                }
            }
            
            contentService.saveContent(content);
            redirectAttributes.addFlashAttribute("successMessage", isNew ? "Content created successfully!" : "Content updated successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to upload image: " + e.getMessage());
            return isNew ? "content-create" : "content-update";
        }

        return "redirect:/internal/contents";
    }

    @GetMapping("/update/{id}")
    public String showUpdateContentForm(@PathVariable(name = "id") Long id, Model model) {
        Content content = contentService.getContentById(id);
        if (content == null) return "redirect:/internal/contents";
        model.addAttribute("content", content);
        return "content-update";
    }

    @GetMapping("/delete/{id}")
    public String deleteContent(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        contentService.deleteContent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Content deleted successfully!");
        return "redirect:/internal/contents";
    }

    @GetMapping("/detail/{id}")
    public String showContentDetail(@PathVariable(name = "id") Long id, Model model) {
        Content content = contentService.getContentById(id);
        if (content == null) return "redirect:/internal/contents";
        model.addAttribute("content", content);
        return "content-detail"; 
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<String> uploadInlineImage(@RequestParam(name = "file") MultipartFile file) {
        try {
            String imageUrl = contentService.saveImageFile(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed");
        }
    }
}