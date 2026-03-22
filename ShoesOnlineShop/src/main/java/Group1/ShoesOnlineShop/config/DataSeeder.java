package Group1.ShoesOnlineShop.config;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // =============== ADMIN ===============
        List<User> mockAdmins = Arrays.asList(
            createUser("admin1", "admin1@shoeweb.com", "Nguyen Van Admin", "0901111111", "123 Quan 1, TP HCM", "123456", "ADMIN"),
            createUser("admin2", "admin2@shoeweb.com", "Tran Thi System", "0902222222", "456 Hoan Kiem, Ha Noi", "123456", "ADMIN"),
            createUser("admin_master", "master@shoeweb.com", "Le Hoang Manager", "0903333333", "789 Hai Chau, Da Nang", "123456", "ADMIN")
        );

        // =============== SALE STAFF ===============
        List<User> mockSales = Arrays.asList(
            createUser("sale1", "sale1@shoeweb.com", "Pham Van Sale", "0911111111", "100 Le Loi, Quan 1, TP HCM", "123456", "SALE_STAFF"),
            createUser("sale2", "sale2@shoeweb.com", "Vo Thi Seller", "0912222222", "200 Tran Hung Dao, Ha Noi", "123456", "SALE_STAFF")
        );

        // =============== MARKETING STAFF ===============
        List<User> mockMarketings = Arrays.asList(
            createUser("marketing1", "mkt1@shoeweb.com", "Hoang Van Marketing", "0921111111", "300 Nguyen Hue, Quan 1, TP HCM", "123456", "MARKETING_STAFF"),
            createUser("marketing2", "mkt2@shoeweb.com", "Le Thi Content", "0922222222", "400 Ba Trieu, Ha Noi", "123456", "MARKETING_STAFF")
        );

        // =============== CUSTOMER ===============
        List<User> mockCustomers = Arrays.asList(
            createUser("customer1", "customer1@gmail.com", "Nguyen Van Khach", "0931111111", "500 Ly Tu Trong, Quan 1, TP HCM", "123456", "CUSTOMER"),
            createUser("customer2", "customer2@gmail.com", "Tran Thi Mua", "0932222222", "600 Phan Chu Trinh, Ha Noi", "123456", "CUSTOMER"),
            createUser("customer3", "customer3@gmail.com", "Le Van Demo", "0933333333", "700 Bach Dang, Da Nang", "123456", "CUSTOMER")
        );

        // Seed all users
        seedUsers(mockAdmins, "ADMIN");
        seedUsers(mockSales, "SALE_STAFF");
        seedUsers(mockMarketings, "MARKETING_STAFF");
        seedUsers(mockCustomers, "CUSTOMER");
    }

    private void seedUsers(List<User> users, String roleName) {
        for (User user : users) {
            if (userRepository.findByUserName(user.getUserName()).isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
                userRepository.save(user);
                System.out.println("Seeded " + roleName + ": " + user.getUserName());
            }
        }
    }

    private User createUser(String username, String email, String fullName, String phone, String address, String rawPassword, String role) {
        User user = new User();
        user.setUserName(username);
        user.setUserEmail(email);
        user.setPasswordHash(rawPassword);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(role);
        user.setIsActive(true);
        return user;
    }
}
