package controllers;

import backend.controllers.AuthController;
import backend.dao.UserDAO;
import backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private AuthController authController;
    private UserDAO mockUserDAO;

    @BeforeEach
    public void setup() {
        mockUserDAO = mock(UserDAO.class);
        authController = new AuthController() {
            {
                this.userDAO = mockUserDAO; // Gán mock dao vào AuthController
            }
        };
    }

    @Test
    public void testRegister_ValidUser_Success() throws Exception {
        User user = new User("test@example.com", "Password123");

        when(mockUserDAO.findByEmail("test@example.com")).thenReturn(null);
        when(mockUserDAO.insert(any(User.class))).thenReturn(true);

        boolean result = authController.register(user);

        assertTrue(result);
        verify(mockUserDAO).insert(any(User.class));
        assertTrue(BCrypt.checkpw("Password123", user.getPassword()));
    }

    @Test
    public void testRegister_InvalidEmail_ThrowsException() {
        User user = new User("invalid-email", "Password123");

        Exception ex = assertThrows(Exception.class, () -> authController.register(user));
        assertEquals("Invalid email format!", ex.getMessage());
    }

    @Test
    public void testRegister_WeakPassword_ThrowsException() {
        User user = new User("test@example.com", "abc");

        Exception ex = assertThrows(Exception.class, () -> authController.register(user));
        assertEquals("Password must be 6-16 characters and contain at least one uppercase letter!", ex.getMessage());
    }

    @Test
    public void testRegister_EmailAlreadyExists_ThrowsException() throws Exception {
        User user = new User("test@example.com", "Password123");

        when(mockUserDAO.findByEmail("test@example.com")).thenReturn(new User());

        Exception ex = assertThrows(Exception.class, () -> authController.register(user));
        assertEquals("The email already exists!", ex.getMessage());
    }
}
