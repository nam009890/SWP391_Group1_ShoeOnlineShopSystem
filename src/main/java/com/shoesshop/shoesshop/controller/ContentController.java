package com.shoesshop.shoesshop.controller;

import com.shoesshop.shoesshop.entity.Content;
import com.shoesshop.shoesshop.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;

@Controller
public class ContentController {

    @Autowired
    private ContentRepository contentRepository;

    // Khởi tạo dữ liệu mẫu (Mock data)
    public ContentController(ContentRepository repo) {
        this.contentRepository = repo;
        if (repo.count() == 0) {
            repo.save(new Content(null, "How to right clean shoe", LocalDate.of(2024, 10, 25)));
            repo.save(new Content(null, "Type of shoe", LocalDate.of(2024, 10, 25)));
            repo.save(new Content(null, "new tech from adidas", LocalDate.of(2024, 10, 25)));
            repo.save(new Content(null, "New degisn from nike", LocalDate.of(2024, 10, 25)));
            repo.save(new Content(null, "Why people like puma", LocalDate.of(2024, 10, 25)));
            
            // Dữ liệu test phân trang
            for (int i = 6; i <= 20; i++) {
                repo.save(new Content(null, "Content Demo " + i, LocalDate.now()));
            }
        }
    }

    @GetMapping("/contents")
    public String listContents(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Content> pageContents;

        if (keyword == null || keyword.isEmpty()) {
            pageContents = contentRepository.findAll(paging);
        } else {
            pageContents = contentRepository.findByNameContainingIgnoreCase(keyword, paging);
        }

        model.addAttribute("contents", pageContents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageContents.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "content-list"; // Trả về giao diện HTML
    }

    // 1. Hiển thị trang Create
    @GetMapping("/contents/create")
    public String showCreateContentForm(Model model) {
        model.addAttribute("content", new Content());
        return "content-create";
    }

    // 2. Hiển thị trang Update
    @GetMapping("/contents/update/{id}")
    public String showUpdateContentForm(@PathVariable Long id, Model model) {
        Content content = contentRepository.findById(id).orElse(null);
        if (content == null) {
            return "redirect:/contents";
        }
        model.addAttribute("content", content);
        return "content-update";
    }

    // 3. Xóa Content
    @GetMapping("/contents/delete/{id}")
    public String deleteContent(@PathVariable Long id) {
        contentRepository.deleteById(id);
        return "redirect:/contents";
    }
}