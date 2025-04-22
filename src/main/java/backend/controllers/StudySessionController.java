package backend.controllers;

import backend.dao.StudySessionDAO;
import backend.models.StudySession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class StudySessionController {

    private final StudySessionDAO dao = new StudySessionDAO();

    /** Lưu một phiên Pomodoro */
    public void savePomodoro(int userId, LocalDateTime start, LocalDateTime end) {
        int minutes = (int) Duration.between(start, end).toMinutes();
        dao.insert(new StudySession(userId, start, end, minutes));
    }

    /** Lấy toàn bộ session của user */
    public List<StudySession> getSessions(int userId) {
        return dao.findByUser(userId);
    }

    /** Tổng phút học của hôm nay */
    public int todayMinutes(int userId) {
        String today = LocalDateTime.now().toLocalDate().toString(); // yyyy-MM-dd
        return dao.totalMinutesByDate(userId, today);
    }
}
