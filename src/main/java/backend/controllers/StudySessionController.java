package backend.controllers;

import backend.dao.StudySessionDAO;
import backend.models.StudySession;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StudySessionController {
    private final StudySessionDAO dao = new StudySessionDAO();

    public void logSession(int userId, int durationMinutes) {
        StudySession session = new StudySession(userId, LocalDate.now(), durationMinutes);
        dao.insertSession(session);
    }

    public double getTotalStudyHours(int userId) {
        return dao.getSessionsByUser(userId).stream()
                .mapToInt(StudySession::getDurationMinutes)
                .sum() / 60.0;
    }

    public double getAverageHoursPerDay(int userId) {
        Map<LocalDate, Integer> dailyTotals = new HashMap<>();

        for (StudySession session : dao.getSessionsByUser(userId)) {
            dailyTotals.merge(session.getDate(), session.getDurationMinutes(), Integer::sum);
        }

        return dailyTotals.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0) / 60.0;
    }

    public int getStreak(int userId) {
        Set<LocalDate> activeDays = dao.getSessionsByUser(userId).stream()
                .collect(Collectors.groupingBy(StudySession::getDate,
                        Collectors.summingInt(StudySession::getDurationMinutes)))
                .entrySet().stream()
                .filter(e -> e.getValue() >= 10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        int streak = 0;
        LocalDate today = LocalDate.now();

        while (activeDays.contains(today.minusDays(streak))) {
            streak++;
        }

        return streak;
    }
}
