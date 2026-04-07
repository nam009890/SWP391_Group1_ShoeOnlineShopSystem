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
            createUser("sale3", "sale3@shoeweb.com", "Pham Van Hung", "0911111114", "101 Le Loi, Quan 1, TP HCM", "123456", "SALE_STAFF"),
            createUser("sale4", "sale4@shoeweb.com", "Vo Thi Lan", "0912222227", "201 Tran Hung Dao, Ha Noi", "123456", "SALE_STAFF"),
            createUser("sale5", "sale5@shoeweb.com", "Nguyen Van Minh", "0913333333", "202 Le Duan, Da Nang", "123456", "SALE_STAFF"),
            createUser("sale6", "sale6@shoeweb.com", "Tran Thi Hoa", "0914444444", "203 Nguyen Trai, Ha Noi", "123456", "SALE_STAFF")
        );

        // =============== MARKETING STAFF ===============
        List<User> mockMarketings = Arrays.asList(
            createUser("marketing1", "mkt1@shoeweb.com", "Hoang Van Marketing", "0921111111", "300 Nguyen Hue, Quan 1, TP HCM", "123456", "MARKETING_STAFF"),
            createUser("marketing2", "mkt2@shoeweb.com", "Le Thi Content", "0922222222", "400 Ba Trieu, Ha Noi", "123456", "MARKETING_STAFF")
        );
        
        List<User> mockShippers = Arrays.asList(
            createUser("shipper1", "shipper1@shoeweb.com", "Pham Van Hung", "0941111112", "Trinh Dinh Trong, Ha Noi", "123456", "SHIPPER"),
                 createUser("shipper2", "shipper2@shoeweb.com", "Tran Thi Hoa", "0941111113", "Trinh Dinh Trong, Ha Noi", "123456", "SHIPPER"),
                  createUser("shipper3", "shipper3@shoeweb.com", "Nguyen Van Minh", "0941111114", "Trinh Dinh Trong, Ha Noi", "123456", "SHIPPER")
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
        seedUsers(mockShippers, "SHIPPER");
    }

    private void seedUsers(List<User> users, String roleName) {
        for (User user : users) {
            try {
                if (userRepository.findByUserName(user.getUserName()).isEmpty() && 
                    userRepository.findByUserEmail(user.getUserEmail()).isEmpty()) {
                    user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
                    userRepository.save(user);
                    System.out.println("Seeded " + roleName + ": " + user.getUserName());
                } else {
                    System.out.println("Skipped seeding " + roleName + ": " + user.getUserName() + " (Username or Email already exists)");
                }
            } catch (Exception e) {
                System.out.println("Error seeding " + roleName + ": " + user.getUserName() + " - " + e.getMessage());
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
