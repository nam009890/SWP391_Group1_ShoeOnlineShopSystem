/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Group1.ShoesOnlineShop.controller;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Administrator
 */
@Controller
public class RegisterController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @PostMapping("/register")
    public String processRegister(@RequestParam("username") String username, 
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model) {
        
        // 1. GỌI HÀM KIỂM TRA TỒN TẠI
        boolean isUserExist = userRepository.existsByUserName(username);
        if (isUserExist) {
            return "redirect:/register?error=exists";
        }
        if(!password.equals(confirmPassword)){
            return "redirect:/register?error=unmatched";
        }

        // 2. Nếu chưa tồn tại -> Tiến hành mã hóa mật khẩu và lưu xuống DB
        System.out.println("Tài khoản hợp lệ, tiến hành lưu...");
        // Code lưu user của bạn ở đây...

        return "redirect:/login"; // Chuyển hướng sang trang đăng nhập
    }
}
