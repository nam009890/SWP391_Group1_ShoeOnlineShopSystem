package com.shoesshop.shoesshop.repository;

import com.shoesshop.shoesshop.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
    // Hàm tìm kiếm theo tên bài viết
    Page<Content> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}