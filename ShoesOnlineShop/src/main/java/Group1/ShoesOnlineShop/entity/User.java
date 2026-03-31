package Group1.ShoesOnlineShop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    private String userName;

    // THÊM VALIDATE EMAIL
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(name = "user_email", unique = true, nullable = false, length = 100)
    private String userEmail;

    @Column(name = "password_hash", nullable = true, length = 255)
    private String passwordHash;

    // OAuth2 Support
    @Column(name = "auth_provider", length = 20)
    private String authProvider; // e.g., "LOCAL", "GOOGLE"

    @Column(name = "provider_id", length = 50)
    private String providerId;

    // THÊM VALIDATE TÊN
    @NotBlank(message = "Full Name cannot be blank")
    @Size(min = 5, max = 255, message = "Full Name must be between 5 and 255 characters")
    @Pattern(regexp = "^(?!\\s*$)[a-zA-ZÀ-ỹ\\s']+$", message = "Name must not be only spaces and cannot contain numbers or special characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits, no letters or special characters")
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Address cannot be only spaces")
    @Column(name = "address", columnDefinition = "NVARCHAR(MAX)")
    private String address;

    @Column(name = "user_role", nullable = false, length = 50)
    private String userRole;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "reset_token", length = 100)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // Quan hệ với các bảng khác

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cart> carts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    // Audit fields

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}