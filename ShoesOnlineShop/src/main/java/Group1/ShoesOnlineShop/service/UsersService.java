/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.repository.UserRepository;
import Group1.ShoesOnlineShop.entity.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Administrator
 */
@Service
public class UsersService {

    @Autowired
    private UserRepository userRepository; // Gọi công cụ kết nối Database

    // Hàm này nhận vào username và password người dùng gõ trên form HTML
    public boolean checkLoginCustom(String inputUsername, String inputPassword) {

        // 1. Lấy dữ liệu User từ Database dựa vào username
        User optionalUser = userRepository.findByUserEmail(inputUsername);

        // 2. Kiểm tra xem user có tồn tại không
//        if (optionalUser.) {
//            User userFromDB = optionalUser.get(); // Rút đối tượng User từ trong Optional ra
//            
//            // Lấy mật khẩu đã mã hóa từ Database
//            String hashedPasswordFromDB = userFromDB.getPassword(); 
//
//            // 3. So sánh mật khẩu người dùng gõ (inputPassword) với mật khẩu trong DB
//            // Hàm matches() sẽ tự động đem inputPassword đi mã hóa rồi so sánh
//            boolean isPasswordMatch = passwordEncoder.matches(inputPassword, hashedPasswordFromDB);
//
//            if (isPasswordMatch) {
//                System.out.println("Đăng nhập thành công!");
//                return true;
//            } else {
//                System.out.println("Sai mật khẩu!");
//                return false;
//            }
//        } else {
//            System.out.println("Tài khoản không tồn tại trong Database!");
//            return false;
//        }
        return true;
    }
}
