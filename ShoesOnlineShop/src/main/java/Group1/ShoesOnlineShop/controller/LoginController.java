/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Group1.ShoesOnlineShop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Administrator
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String checkLogin(@RequestParam("username") String user,
                            @RequestParam("password") String pass) {
        System.out.println("Tài khoản: " + user);
        System.out.println("Mật khẩu: " + pass);
        return "index";
    }

    @GetMapping("/")
    public String homePage() {
        return "index"; // Bắt buộc phải có file index.html trong thư mục templates
    }
}
