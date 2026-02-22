package com.shoesshop.shoesshop.service;

import com.shoesshop.shoesshop.entity.Content;
import com.shoesshop.shoesshop.repository.ContentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @PostConstruct
    public void initMockData() {
        if (contentRepository.count() == 0) {
            contentRepository.save(new Content(null, "How to right clean shoe", "HeadLine 1", "Main content here...", LocalDate.of(2024, 10, 25)));
            contentRepository.save(new Content(null, "Type of shoe", "HeadLine 2", "Main content here...", LocalDate.of(2024, 10, 25)));
            contentRepository.save(new Content(null, "new tech from adidas", "HeadLine 3", "Main content here...", LocalDate.of(2024, 10, 25)));
            contentRepository.save(new Content(null, "New degisn from nike", "HeadLine 4", "Main content here...", LocalDate.of(2024, 10, 25)));
            contentRepository.save(new Content(null, "Why people like puma", "HeadLine 5", "Main content here...", LocalDate.of(2024, 10, 25)));
            for (int i = 6; i <= 20; i++) {
                contentRepository.save(new Content(null, "Content Demo " + i, "Headline " + i, "Main content " + i, LocalDate.now()));
            }
        }
    }

    public Page<Content> getContents(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return contentRepository.findAll(paging);
        } else {
            return contentRepository.findByNameContainingIgnoreCase(keyword, paging);
        }
    }

    public void saveContent(Content content) {
        if (content.getCreateDate() == null) {
            content.setCreateDate(LocalDate.now());
        }
        contentRepository.save(content);
    }

    public Content getContentById(Long id) {
        return contentRepository.findById(id).orElse(null);
    }

    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }
}