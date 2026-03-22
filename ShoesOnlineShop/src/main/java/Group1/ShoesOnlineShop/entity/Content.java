/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(name = "content_title", nullable = false, length = 200)
    private String contentTitle;

    @Column(name = "content_text", columnDefinition = "NVARCHAR(MAX)")
    private String contentText;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "content_type", nullable = false, length = 20)
    private String contentType;



    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

  // 1. Constructor không tham số (Bắt buộc)
    public Content() {
    }

    // 2. Constructor có tham số
    public Content(Long id, String contentTitle, String contentText, String imageUrl, String contentType, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contentTitle = contentTitle;
        this.contentText = contentText;
        this.imageUrl = imageUrl;
        this.contentType = contentType;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // 3. GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContentTitle() { return contentTitle; }
    public void setContentTitle(String contentTitle) { this.contentTitle = contentTitle; }

    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }



    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}