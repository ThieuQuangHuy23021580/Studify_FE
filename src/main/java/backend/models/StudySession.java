package backend.models;

import java.time.LocalDateTime;

public class StudySession {
    private int id;
    private int userId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int durationMinutes;

    public StudySession() {}

    public StudySession(int userId, LocalDateTime startedAt, LocalDateTime endedAt, int durationMinutes) {
        this.userId = userId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationMinutes = durationMinutes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}
