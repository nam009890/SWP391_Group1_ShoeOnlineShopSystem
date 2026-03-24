package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean isEmailExists(String email, Long id) {
        return userRepository.existsByUserEmailAndUserIdNot(email, id);
    }

    public void updateUserProfile(User updatedUser) {
        // Lấy dữ liệu cũ từ DB lên để không ghi đè mất Password hay Role
        User existingUser = getUserById(updatedUser.getUserId());
        if (existingUser != null) {
            existingUser.setFullName(updatedUser.getFullName());
            // Email is NOT updated — it is immutable
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());
            userRepository.save(existingUser);
        }
    }

    public String changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        if (user == null) return "User not found!";
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return "Incorrect current password!";
        }
        if (newPassword == null || newPassword.length() < 6) {
            return "New password must be at least 6 characters!";
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return null; // null = success
    }

    public void registerCustomer(User user) {
        if (user.getUserEmail() == null || !user.getUserEmail().toLowerCase().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email phải sử dụng định dạng @gmail.com.");
        }
        if (user.getUserEmail().length() > 100) {
            throw new IllegalArgumentException("Email không được vượt quá 100 ký tự theo CSDL.");
        }
        if (user.getUserName() == null || user.getUserName().trim().isEmpty() || user.getUserName().contains(" ")) {
            throw new IllegalArgumentException("Username không được để trống và không được chứa khoảng trắng.");
        }
        if (user.getUserName().length() > 50) {
            throw new IllegalArgumentException("Username không được vượt quá 50 ký tự theo CSDL.");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().length() < 4) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 4 ký tự.");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ và tên không được để trống.");
        }
        if (user.getFullName().length() > 100) {
            throw new IllegalArgumentException("Họ và tên không được vượt quá 100 ký tự theo CSDL.");
        }
        if (user.getPhone() != null && user.getPhone().length() > 20) {
            throw new IllegalArgumentException("Số điện thoại không được vượt quá 20 ký tự theo CSDL.");
        }

        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            throw new IllegalArgumentException("Email này đã được sử dụng.");
        }
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            throw new IllegalArgumentException("Username này đã tồn tại.");
        }
        
        user.setUserRole("CUSTOMER");
        user.setIsActive(true);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);
    }
}