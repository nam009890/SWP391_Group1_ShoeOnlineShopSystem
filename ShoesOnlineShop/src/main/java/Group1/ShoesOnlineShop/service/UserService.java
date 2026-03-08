package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
            existingUser.setUserEmail(updatedUser.getUserEmail());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());
            userRepository.save(existingUser);
        }
    }
}