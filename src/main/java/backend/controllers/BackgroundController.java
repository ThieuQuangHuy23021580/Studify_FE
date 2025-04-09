package backend.controllers;

import backend.dao.BackgroundDAO;
import backend.dao.UserDAO;
import backend.models.Background;

import java.util.List;

public class BackgroundController {
    private BackgroundDAO backgroundDAO;
    private UserDAO userDAO;

    public BackgroundController() {
        backgroundDAO = new BackgroundDAO();
        userDAO = new UserDAO();
    }

    public List<Background> getAllBackgrounds() {
        return backgroundDAO.getAllBackgrounds();
    }

    public List<Background> getBackgroundsByCategory(String category) {
        return backgroundDAO.getBackgroundsByCategory(category);
    }

    public boolean setUserBackground(int userId, int backgroundId) {
        return userDAO.setUserBackground(userId, backgroundId);
    }

    public Background getUserBackground(int userId) {
        return backgroundDAO.getBackgroundById(userDAO.getUserBackgroundId(userId));
    }
}
