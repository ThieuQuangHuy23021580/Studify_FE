package backend.controllers;

import backend.dao.UserDAO;
import backend.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class AuthController {
    protected UserDAO userDAO;

    public AuthController() {
        userDAO = new UserDAO();
    }

    /**
     * Đăng nhập
     * @param email Email của người dùng
     * @param password Mật khẩu của người dùng
     * */
    public User login(String email, String password) throws Exception {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            throw new Exception("The username doesn't exist!");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Wrong username or password!");
        }

        return user;
    }

    /** Đăng ký tài khoản */
    public boolean register(User user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new Exception("Invalid email format!");
        }

        if (password.length() < 6 || password.length() > 16 || !password.matches(".*[A-Z].*")) {
            throw new Exception("Password must be 6-16 characters and contain at least one uppercase letter!");
        }

        if (userDAO.findByEmail(email) != null) {
            throw new Exception("The email already exists!");
        }

        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashed);

        return userDAO.insert(user);
    }
}