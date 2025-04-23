package backend.models;

import java.time.LocalDate;

public class StudySession {
    private int id;
    private int userId;
    private LocalDate date;
    private int durationMinutes;

    public StudySession(int userId, LocalDate date, int durationMinutes) {
        this.userId = userId;
        this.date = date;
        this.durationMinutes = durationMinutes;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public int getDurationMinutes() { return durationMinutes; }

    public void setId(int id) { this.id = id; }
}
