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
        user.setUserRole("CUSTOMER");

        when(adminUserRepository.existsByUserEmail("test@example.com")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("fullName"));
        assertEquals("Full name cannot be blank!", errors.get("fullName"));
    }

    // 6. VALIDATION - Email trống
    @Test
    void testValidateUser_BlankEmail() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userEmail"));
    }

    // 7. VALIDATION - Email sai định dạng
    @Test
    void testValidateUser_InvalidEmail() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("not-an-email");
        user.setUserRole("CUSTOMER");

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userEmail"));
        assertEquals("Invalid email format!", errors.get("userEmail"));
    }

    // 8. VALIDATION - Email đã tồn tại
    @Test
    void testValidateUser_DuplicateEmail() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("existing@example.com");
        user.setUserRole("CUSTOMER");

        when(adminUserRepository.existsByUserEmail("existing@example.com")).thenReturn(true);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("userEmail"));
        assertEquals("This email is already registered!", errors.get("userEmail"));
    }

    // 9. VALIDATION - Phone sai định dạng
    @Test
    void testValidateUser_InvalidPhone() {
        User user = new User();
        user.setFullName("Test User");
        user.setUserEmail("valid@example.com");
        user.setUserRole("CUSTOMER");
        user.setPhone("123abc");

        when(adminUserRepository.existsByUserEmail("valid@example.com")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.containsKey("phone"));
        assertEquals("Phone number must be 10-11 digits!", errors.get("phone"));
    }

    // 10. VALIDATION - User hợp lệ, không có lỗi
    @Test
    void testValidateUser_Valid_NoErrors() {
        User user = new User();
        user.setFullName("Nguyen Van B");
        user.setUserEmail("vanb@example.com");
        user.setUserRole("STAFF");
        user.setPhone("0987654321");

        when(adminUserRepository.existsByUserEmail("vanb@example.com")).thenReturn(false);

        Map<String, String> errors = adminUserService.validateUser(user);

        assertTrue(errors.isEmpty());
    }

    // 11. UPDATE ROLE
    @Test
    void testUpdateUserRole_Success() {
        User user = new User();
        user.setUserId(1L);
        user.setUserRole("CUSTOMER");

        when(adminUserRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUserService.updateUserRole(1L, "STAFF");

        assertEquals("STAFF", user.getUserRole());
        verify(adminUserRepository, times(1)).save(user);
    }

    // 12. COUNT USERS
    @Test
    void testCountAllUsers() {
        when(adminUserRepository.countAllUsers()).thenReturn(42L);
        assertEquals(42L, adminUserService.countAllUsers());
    }
}
