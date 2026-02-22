package com.shoesshop.shoesshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "sliders")
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Không được để trống và tối đa 255 ký tự
    @NotBlank(message = "Name of slider cannot be empty")
    @Size(max = 255, message = "Name must be less than 255 characters")
    @Column(name = "slider_name", nullable = false)
    private String name;

    // Mô tả có thể trống, nhưng nếu nhập thì không quá 500 ký tự
    @Size(max = 500, message = "Description is too long")
    private String description;

    private LocalDate createDate;

    // Constructor không tham số
    public Slider() {
    }

    // Constructor có tham số
    public Slider(Long id, String name, String description, LocalDate createDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createDate = createDate;
    }

    // GETTER & SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getCreateDate() { return createDate; }
    public void setCreateDate(LocalDate createDate) { this.createDate = createDate; }
}