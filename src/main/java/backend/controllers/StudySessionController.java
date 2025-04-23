package backend.controllers;

import backend.dao.StudySessionDAO;
import backend.models.Database;
import backend.models.StudySession;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class StudySessionController {
    private final StudySessionDAO studySessionDAO;

    public StudySessionController(Connection conn) throws SQLException {
        this.studySessionDAO = new StudySessionDAO(conn);
        studySessionDAO.createTableIfNotExists();
    }

    public void logStudyTime(int userId, int durationMinutes) {
        StudySession session = new StudySession(userId, LocalDate.now(), durationMinutes);
        try {
            studySessionDAO.saveSession(session);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalStudyMinutes(int userId) {
        try {
            return studySessionDAO.getTotalMinutes(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getTotalStudyHours(int userId) {
        int totalMinutes = getTotalStudyMinutes(userId);
        return totalMinutes / 60.0;
    }

    public double getAverageStudyMinutes(int userId) {
        try {
            return studySessionDAO.getAverageMinutesPerDay(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getAverageStudyHours(int userId) {
        double averageMinutes = getAverageStudyMinutes(userId);
        return averageMinutes / 60.0;
    }

    public int getCurrentStreak(int userId) {
        try {
            return studySessionDAO.getCurrentStreak(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getMaxStreak(int userId) {
        try {
            return studySessionDAO.getMaxStreak(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}