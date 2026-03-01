/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    public Page<Content> getContents(String keyword, int page, int size) {
        Pageable paging = PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return contentRepository.findAll(paging);
        } else {
            return contentRepository.findByContentTitleContainingIgnoreCase(keyword, paging);
        }
    }

    public void saveContent(Content content) {
        // Đã đổi thành getCreatedAt() và LocalDateTime
        if (content.getCreatedAt() == null) {
            content.setCreatedAt(java.time.LocalDateTime.now());
        }
        content.setUpdatedAt(java.time.LocalDateTime.now());
        contentRepository.save(content);
    }

    public Content getContentById(Long id) {
        return contentRepository.findById(id).orElse(null);
    }

    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }
}