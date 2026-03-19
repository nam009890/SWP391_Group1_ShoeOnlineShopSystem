package Group1.ShoesOnlineShop.dto;

import java.time.LocalDateTime;

public class ActivityDTO {
    private String description;
    private String module;
    private String timeAgo;
    private LocalDateTime createdAt;
    private String colorClass;

    public ActivityDTO() {}

    public ActivityDTO(String description, String module, String timeAgo, LocalDateTime createdAt, String colorClass) {
        this.description = description;
        this.module = module;
        this.timeAgo = timeAgo;
        this.createdAt = createdAt;
        this.colorClass = colorClass;
    }

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getColorClass() { return colorClass; }
    public void setColorClass(String colorClass) { this.colorClass = colorClass; }
}
