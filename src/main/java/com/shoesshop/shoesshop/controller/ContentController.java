package com.shoesshop.shoesshop.controller;

import com.shoesshop.shoesshop.entity.Content;
import com.shoesshop.shoesshop.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/contents")
    public String listContents(
            Model model,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Content> pageContents = contentService.getContents(keyword, page, size);
        model.addAttribute("contents", pageContents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageContents.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "content-list";
    }

    @GetMapping("/contents/create")
    public String showCreateContentForm(Model model) {
        model.addAttribute("content", new Content());
        return "content-create";
    }

    @PostMapping("/contents/save")
    public String saveContent(@Valid @ModelAttribute("content") Content content, BindingResult result) {
        // Nếu nhập thiếu Tên bài, Tiêu đề hoặc Nội dung
        if (result.hasErrors()) {
            return content.getId() == null ? "content-create" : "content-update";
        }
        
        contentService.saveContent(content);
        return "redirect:/contents";
    }

    @GetMapping("/contents/update/{id}")
    public String showUpdateContentForm(@PathVariable Long id, Model model) {
        Content content = contentService.getContentById(id);
        if (content == null) {
            return "redirect:/contents";
        }
        model.addAttribute("content", content);
        return "content-update";
    }

    @GetMapping("/contents/delete/{id}")
    public String deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return "redirect:/contents";
    }
}