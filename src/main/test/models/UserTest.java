package models;

import backend.models.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testConstructorWithIdEmailPassword() {
        User user = new User(1, "test@example.com", "password123");

        assertEquals(1, user.getUserId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testConstructorWithEmailPassword() {
        User user = new User("test@example.com", "password123");

        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(0, user.getUserId());
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User();
        user.setUserId(42);
        user.setEmail("user@example.com");
        user.setPassword("securepassword");
        user.setBackgroundId(5);

        assertEquals(42, user.getUserId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("securepassword", user.getPassword());
        assertEquals(5, user.getBackgroundId());
    }

    @Test
    public void testToString() {
        User user = new User(99, "user@domain.com", "pass");
        String expected = "User{studentId=99, email='user@domain.com'}";
        assertEquals(expected, user.toString());
    }
}
