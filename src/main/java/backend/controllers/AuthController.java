package backend.controllers;

import backend.dao.UserDAO;
import backend.models.User;
import org.checkerframework.checker.units.qual.A;
import org.mindrot.jbcrypt.BCrypt;

public class AuthController {
    private UserDAO userDAO;

    public AuthController() {
        userDAO = new UserDAO();
    }

    public User login(String username, String password) throws Exception {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            throw new Exception("The username doesn't exist!");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new Exception("Wrong username or password!");
        }

        return user;
    }

    public boolean register(User user) throws Exception {
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new Exception("The username already exists!");
        }

        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);

        return userDAO.insert(user);
    }

    public static void main(String[] args) throws Exception {
        User user = new User("xochimcu12323", "manhmanh20", "aduijidfd@gmail.com");
        AuthController ac = new AuthController();
        ac.register(user);
    }
}