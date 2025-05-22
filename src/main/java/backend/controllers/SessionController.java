package backend.controllers;

import backend.dao.SessionDAO;
import backend.models.Session;

import java.util.List;

public class SessionController {
    protected SessionDAO sessionDAO = new SessionDAO();

    public boolean createNewSession(String sessionId, String userId) {
        if (sessionDAO.sessionExists(sessionId)) {
            return false;
        }
        return sessionDAO.createSession(new Session(sessionId, userId));
    }

    public List<Session> getSessionsForUser(String userId) {
        return sessionDAO.getAllSessionsByUser(userId);
    }

    public boolean sessionExists(String sessionId) {
        return sessionDAO.sessionExists(sessionId);
    }

    public String getUserIdBySessionId(String sessionId) {
        return sessionDAO.getUserIdBySessionId(sessionId);
    }

    public static void main(String[] args) {
        SessionController sc = new SessionController();
        sc.createNewSession("coding", "32");
    }
}