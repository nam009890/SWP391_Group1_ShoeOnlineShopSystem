package Group1.ShoesOnlineShop.service;

import Group1.ShoesOnlineShop.entity.User;
import Group1.ShoesOnlineShop.repository.AdminUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    // 1. SUCCESSFUL - Lưu user
    @Test
    void testSaveUser_Success() {
        User user = new User();
        user.setFullName("Nguyen Van A");
        user.setUserEmail("vana@example.com");
        user.setUserRole("CUSTOMER");

        adminUserService.saveUser(user);
        verify(adminUserRepository, times(1)).save(user);
    }

    // 2. TOGGLE BLOCK - Block user đang active
    @Test
    void testToggleBlock_BlockActiveUser() {
        User user = new User();
        user.setUserId(1L);
        user.setIsActive(true);

        when(adminUserRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUserService.toggleBlock(1L);

        assertFalse(user.getIsActive());
        verify(adminUserRepository, times(1)).save(user);
    }

    // 3. TOGGLE BLOCK - Unblock user đang bị block
    @Test
    void testToggleBlock_UnblockBlockedUser() {
        User user = new User();
        user.setUserId(1L);
        user.setIsActive(false);

        when(adminUserRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUserService.toggleBlock(1L);

        assertTrue(user.getIsActive());
    }

    // 4. TOGGLE BLOCK - User không tồn tại
    @Test
    void testToggleBlock_UserNotFound_NoException() {
        when(adminUserRepository.findById(999L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> adminUserService.toggleBlock(999L));
        verify(adminUserRepository, never()).save(any());
    }

    // 5. VALIDATION - Full name trống
    @Test
    void testValidateUser_BlankFullName() {
        User user = new User();
        user.setFullName("");
        user.setUserEmail("test@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("fullName"));
        assertEquals("Full name cannot be blank!", errors.get("fullName"));
    }

    // 6. VALIDATION - Full name chứa số
    @Test
    void testValidateUser_FullNameWithNumbers() {
        User user = new User();
        user.setFullName("Nguyen Van 123");
        user.setUserEmail("test@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("fullName"));
        assertEquals("Full name must contain only letters and spaces, no numbers or special characters!", errors.get("fullName"));
    }

    // 7. VALIDATION - Full name chứa ký tự đặc biệt
    @Test
    void testValidateUser_FullNameWithSpecialChars() {
        User user = new User();
        user.setFullName("Test @#$");
        user.setUserEmail("test@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("fullName"));
    }

    // 8. VALIDATION - Email trống
    @Test
    void testValidateUser_BlankEmail() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userEmail"));
    }

    // 9. VALIDATION - Email sai định dạng
    @Test
    void testValidateUser_InvalidEmail() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("not-an-email");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userEmail"));
        assertEquals("Invalid email format!", errors.get("userEmail"));
    }

    // 10. VALIDATION - Email đã tồn tại
    @Test
    void testValidateUser_DuplicateEmail() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("existing@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");

        when(adminUserRepository.existsByUserEmail("existing@example.com")).thenReturn(true);
        when(adminUserRepository.existsByUserName("testuser")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userEmail"));
        assertEquals("This email is already registered!", errors.get("userEmail"));
    }

    // 11. VALIDATION - Phone không bắt đầu bằng 0
    @Test
    void testValidateUser_PhoneNotStartingWith0() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");
        user.setPhone("1234567890");

        when(adminUserRepository.existsByUserEmail("valid@example.com")).thenReturn(false);
        when(adminUserRepository.existsByUserName("testuser")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("phone"));
        assertEquals("Phone number must be exactly 10 digits and start with 0!", errors.get("phone"));
    }

    // 12. VALIDATION - Phone chứa ký tự chữ
    @Test
    void testValidateUser_PhoneWithLetters() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");
        user.setPhone("123abc");

        when(adminUserRepository.existsByUserEmail("valid@example.com")).thenReturn(false);
        when(adminUserRepository.existsByUserName("testuser")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("phone"));
        assertEquals("Phone number must be exactly 10 digits and start with 0!", errors.get("phone"));
    }

    // 13. VALIDATION - Phone 9 chữ số (quá ngắn)
    @Test
    void testValidateUser_PhoneTooShort() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");
        user.setPhone("098765432");

        when(adminUserRepository.existsByUserEmail("valid@example.com")).thenReturn(false);
        when(adminUserRepository.existsByUserName("testuser")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("phone"));
    }

    // 14. VALIDATION - Username chứa ký tự đặc biệt
    @Test
    void testValidateUser_UsernameWithSpecialChars() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("test@user!");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userName"));
        assertEquals("Username can only contain letters, numbers, and underscores!", errors.get("userName"));
    }

    // 15. VALIDATION - Username quá ngắn
    @Test
    void testValidateUser_UsernameTooShort() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("ab");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userName"));
        assertEquals("Username must be between 3 and 50 characters!", errors.get("userName"));
    }

    // 16. VALIDATION - Username trống
    @Test
    void testValidateUser_UsernameBlank() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userName"));
        assertEquals("Username cannot be blank!", errors.get("userName"));
    }

    // 17. VALIDATION - Address chỉ toàn ký tự đặc biệt
    @Test
    void testValidateUser_AddressOnlySpecialChars() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");
        user.setAddress("!@#$%^&*()!@#$");

        when(adminUserRepository.existsByUserEmail("valid@example.com")).thenReturn(false);
        when(adminUserRepository.existsByUserName("testuser")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("address"));
    }

    // 18. VALIDATION - Address quá ngắn
    @Test
    void testValidateUser_AddressTooShort() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserName("testuser");
        user.setUserRole("CUSTOMER");
        user.setAddress("short");

        when(adminUserRepository.existsByUserEmail("valid@example.com")).thenReturn(false);
        when(adminUserRepository.existsByUserName("testuser")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("address"));
        assertEquals("Address must be between 10 and 255 characters!", errors.get("address"));
    }

    // 19. VALIDATION - User hợp lệ, không có lỗi
    @Test
    void testValidateUser_Valid_NoErrors() {
        User user = new User();
        user.setFullName("Nguyen Van B");
        user.setUserEmail("vanb@example.com");
        user.setUserName("vanbuser");
        user.setUserRole("SALE_STAFF");
        user.setPhone("0987654321");

        when(adminUserRepository.existsByUserEmail("vanb@example.com")).thenReturn(false);
        when(adminUserRepository.existsByUserName("vanbuser")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.isEmpty());
    }

    // 20. UPDATE ROLE
    @Test
    void testUpdateUserRole_Success() {
        User user = new User();
        user.setUserId(1L);
        user.setUserRole("CUSTOMER");

        when(adminUserRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUserService.updateUserRole(1L, "SALE_STAFF");

        assertEquals("SALE_STAFF", user.getUserRole());
        verify(adminUserRepository, times(1)).save(user);
    }

    // 21. COUNT USERS
    @Test
    void testCountAllUsers() {
        when(adminUserRepository.countAllUsers()).thenReturn(42L);
        assertEquals(42L, adminUserService.countAllUsers());
    }
}
