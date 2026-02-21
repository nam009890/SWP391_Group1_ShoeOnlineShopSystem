package com.shoesshop.shoesshop;

import com.shoesshop.shoesshop.entity.Coupon;
import com.shoesshop.shoesshop.repository.CouponRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(CouponRepository repo) {
        return args -> {
            // Chỉ tạo nếu database chưa có gì (hoặc ít quá)
            if (repo.count() < 5) {
                System.out.println("Dang tao 20 du lieu mau (Mock Data) de test phan trang...");

                // 1. Tạo vài cái có tên đẹp để demo
                repo.save(new Coupon(null, "Opening Sale", "OPEN2026", 10, LocalDate.now(), LocalDate.now().plusDays(30), true));
                repo.save(new Coupon(null, "Summer Vibes", "SUMMER50", 50, LocalDate.now(), LocalDate.now().plusDays(60), true));
                repo.save(new Coupon(null, "Black Friday", "FRIDAY", 45, LocalDate.now(), LocalDate.now().plusDays(5), true));

                // 2. Dùng vòng lặp tạo thêm 15 cái nữa cho đủ trang
                for (int i = 1; i <= 15; i++) {
                    String name = "Discount Event " + i;
                    String code = "CODE" + i + "TEST"; // VD: CODE1TEST, CODE2TEST...
                    int discount = 5 + (i % 9) * 5;    // Random discount: 5, 10, 15... 45
                    
                    repo.save(new Coupon(null, name, code, discount, LocalDate.now(), LocalDate.now().plusDays(10 + i), true));
                }
                
                System.out.println("Da tao xong Tong cong: " + repo.count() + " coupon!");
            }
        };
    }
}