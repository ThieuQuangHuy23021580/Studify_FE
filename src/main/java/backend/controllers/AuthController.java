package backend.controllers;

import backend.dao.UserDAO;
import backend.models.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthController {
    private UserDAO userDAO;

    public AuthController() {
        userDAO = new UserDAO();
    }

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

    public boolean register(User user) throws Exception {
        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new Exception("The username already exists!");
        }

        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);

        return userDAO.insert(user);
    }
}