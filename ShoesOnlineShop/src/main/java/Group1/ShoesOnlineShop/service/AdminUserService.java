package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.AdminUserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    // === Regex Constants ===
    private static final String FULL_NAME_REGEX = "^[\\p{L}\\s]+$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]+$";
    private static final String PHONE_REGEX = "^0\\d{9}$";
    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String ADDRESS_FORMAT_REGEX = "^[\\p{L}0-9\\s.,/\\-#]+$";
    private static final String ADDRESS_CONTENT_REGEX = ".*[\\p{L}0-9].*";

    // === GET LIST WITH FILTER & PAGINATION ===
    public Page<User> getUsers(String keyword, String role, Boolean isActive, int page, int size, Long excludeUserId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Exclude the current admin from results
            if (excludeUserId != null) {
                predicates.add(cb.notEqual(root.get("userId"), excludeUserId));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.toLowerCase() + "%";
                Predicate emailMatch = cb.like(cb.lower(root.get("userEmail")), like);
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), like);
                Predicate userNameMatch = cb.like(cb.lower(root.get("userName")), like);
                predicates.add(cb.or(emailMatch, nameMatch, userNameMatch));
            }

            if (role != null && !role.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("userRole"), role));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return adminUserRepository.findAll(spec, pageable);
    }

    // === GET BY ID ===
    public User getUserById(Long id) {
        return adminUserRepository.findById(id).orElse(null);
    }

    // === SAVE USER (Update role/status) ===
    public void saveUser(User user) {
        adminUserRepository.save(user);
    }

    // === TOGGLE BLOCK/UNBLOCK ===
    public void toggleBlock(Long id) {
        User user = adminUserRepository.findById(id).orElse(null);
        if (user != null) {
            user.setIsActive(!user.getIsActive());
            adminUserRepository.save(user);
        }
    }

    // === UPDATE ROLE ===
    public void updateUserRole(Long id, String newRole) {
        User user = adminUserRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUserRole(newRole);
            adminUserRepository.save(user);
        }
    }

    // === COUNTS ===
    public long countAllUsers() {
        return adminUserRepository.countAllUsers();
    }

    public long countActiveUsers() {
        return adminUserRepository.countActiveUsers();
    }

    // === VALIDATION ===
    public Map<String, String> validateUser(User user) {
        Map<String, String> errors = new HashMap<>();

        // --- Full Name ---
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Full name cannot be blank!");
        } else {
            String trimmedName = user.getFullName().trim();
            if (trimmedName.length() < 2 || trimmedName.length() > 100) {
                errors.put("fullName", "Full name must be between 2 and 100 characters!");
            } else if (!trimmedName.matches(FULL_NAME_REGEX)) {
                errors.put("fullName", "Full name must contain only letters and spaces, no numbers or special characters!");
            }
        }

        // --- Email ---
        if (user.getUserEmail() == null || user.getUserEmail().trim().isEmpty()) {
            errors.put("userEmail", "Email cannot be blank!");
        } else {
            String trimmedEmail = user.getUserEmail().trim();
            if (trimmedEmail.length() > 100) {
                errors.put("userEmail", "Email must not exceed 100 characters!");
            } else if (!trimmedEmail.matches(EMAIL_REGEX)) {
                errors.put("userEmail", "Invalid email format!");
            } else {
                boolean emailDuplicate = (user.getUserId() == null)
                        ? adminUserRepository.existsByUserEmail(trimmedEmail)
                        : adminUserRepository.existsByUserEmailAndUserIdNot(trimmedEmail, user.getUserId());
                if (emailDuplicate) {
                    errors.put("userEmail", "This email is already registered!");
                }
            }
        }

        // --- Username ---
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            errors.put("userName", "Username cannot be blank!");
        } else {
            String trimmedUserName = user.getUserName().trim();
            if (trimmedUserName.length() < 3 || trimmedUserName.length() > 50) {
                errors.put("userName", "Username must be between 3 and 50 characters!");
            } else if (!trimmedUserName.matches(USERNAME_REGEX)) {
                errors.put("userName", "Username can only contain letters, numbers, and underscores!");
            } else {
                boolean userNameDuplicate = (user.getUserId() == null)
                        ? adminUserRepository.existsByUserName(trimmedUserName)
                        : adminUserRepository.existsByUserNameAndUserIdNot(trimmedUserName, user.getUserId());
                if (userNameDuplicate) {
                    errors.put("userName", "This username is already taken!");
                }
            }
        }

        // --- Phone ---
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            String trimmedPhone = user.getPhone().trim();
            if (!trimmedPhone.matches(PHONE_REGEX)) {
                errors.put("phone", "Phone number must be exactly 10 digits and start with 0!");
            }
        }

        // --- Address ---
        if (user.getAddress() != null && !user.getAddress().trim().isEmpty()) {
            String trimmedAddress = user.getAddress().trim();
            if (trimmedAddress.length() < 10 || trimmedAddress.length() > 255) {
                errors.put("address", "Address must be between 10 and 255 characters!");
            } else if (!trimmedAddress.matches(ADDRESS_FORMAT_REGEX)) {
                errors.put("address", "Address contains invalid special characters! Allowed characters: letters, numbers, spaces, commas, dots, slashes, hyphens, and hashes.");
            } else if (!trimmedAddress.matches(ADDRESS_CONTENT_REGEX)) {
                errors.put("address", "Address must contain at least some letters or numbers!");
            }
        }

        // --- Role ---
        if (user.getUserRole() == null || user.getUserRole().trim().isEmpty()) {
            errors.put("userRole", "Role cannot be blank!");
        }

        return errors;
    }
}
