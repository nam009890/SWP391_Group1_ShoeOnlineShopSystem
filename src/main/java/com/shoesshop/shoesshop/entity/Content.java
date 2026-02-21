package com.shoesshop.shoesshop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_name", nullable = false)
    private String name;

    // --- HAI TRƯỜNG MỚI THÊM VÀO ---
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String mainContent;
    // --------------------------------

    private LocalDate createDate;

    public Content() {}

    // Constructor cũ (Để code mock data ở Controller không bị lỗi)
    public Content(Long id, String name, LocalDate createDate) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
    }

    // GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getMainContent() { return mainContent; }
    public void setMainContent(String mainContent) { this.mainContent = mainContent; }

    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }
}