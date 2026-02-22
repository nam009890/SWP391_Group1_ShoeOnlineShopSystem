package com.shoesshop.shoesshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name of content cannot be empty")
    @Size(max = 255, message = "Name must be less than 255 characters")
    @Column(name = "content_name", nullable = false)
    private String name;

    @NotBlank(message = "Head Line cannot be empty")
    @Size(max = 255, message = "Head Line must be less than 255 characters")
    private String headLine;

    @NotBlank(message = "Main Content cannot be empty")
    @Column(columnDefinition = "TEXT") 
    private String mainContent;

    private LocalDate createDate;

    // 1. Constructor không tham số (Bắt buộc cho Spring Boot)
    public Content() {
    }

    // 2. Constructor ĐẦY ĐỦ 5 tham số để Service gọi
    public Content(Long id, String name, String headLine, String mainContent, LocalDate createDate) {
        this.id = id;
        this.name = name;
        this.headLine = headLine;
        this.mainContent = mainContent;
        this.createDate = createDate;
    }

    // ================= GETTER & SETTER =================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHeadLine() { return headLine; }
    public void setHeadLine(String headLine) { this.headLine = headLine; }

    public String getMainContent() { return mainContent; }
    public void setMainContent(String mainContent) { this.mainContent = mainContent; }

    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }
}