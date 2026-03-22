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

    // === GET LIST WITH FILTER & PAGINATION ===
    public Page<User> getUsers(String keyword, String role, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            errors.put("fullName", "Full name cannot be blank!");
        }

        if (user.getUserEmail() == null || user.getUserEmail().trim().isEmpty()) {
            errors.put("userEmail", "Email cannot be blank!");
        } else if (!user.getUserEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errors.put("userEmail", "Invalid email format!");
        } else {
            boolean emailDuplicate = (user.getUserId() == null)
                    ? adminUserRepository.existsByUserEmail(user.getUserEmail())
                    : adminUserRepository.existsByUserEmailAndUserIdNot(user.getUserEmail(), user.getUserId());
            if (emailDuplicate) {
                errors.put("userEmail", "This email is already registered!");
            }
        }

        if (user.getUserName() != null) {
            if (user.getUserName().trim().isEmpty()) {
                errors.put("userName", "Username cannot be blank!");
            } else {
                boolean userNameDuplicate = (user.getUserId() == null)
                        ? adminUserRepository.existsByUserName(user.getUserName())
                        : adminUserRepository.existsByUserNameAndUserIdNot(user.getUserName(), user.getUserId());
                if (userNameDuplicate) {
                    errors.put("userName", "This username is already taken!");
                }
            }
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            if (!user.getPhone().matches("^[0-9]{10,11}$")) {
                errors.put("phone", "Phone number must be 10-11 digits!");
            }
        }

        if (user.getUserRole() == null || user.getUserRole().trim().isEmpty()) {
            errors.put("userRole", "Role cannot be blank!");
        }

        return errors;
    }
}
