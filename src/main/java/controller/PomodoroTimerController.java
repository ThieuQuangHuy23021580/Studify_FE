package controller;

import backend.controllers.StudySessionController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class PomodoroTimerController {
    private Label timerLabel;
    private Timeline timeline;
    private Runnable onFinishCallback;

    private int initialSeconds;
    private int remainingSeconds;
    private String initialTimeString;

    private StudySessionController studySessionController;
    private int userId;
    private int sessionDuration;

    public PomodoroTimerController(Label timerLabel, String initialTime,
                                   Runnable onFinishCallback,
                                   StudySessionController studySessionController,
                                   int userId) {
        this.timerLabel = timerLabel;
        this.onFinishCallback = onFinishCallback;
        this.studySessionController = studySessionController;
        this.userId = userId;
        setTime(initialTime);
        this.sessionDuration = 0;
        updateLabel();
    }

    private void setTime(String timeString) {
        this.initialTimeString = timeString;
        String[] timeParts = timeString.split(" : ");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        int second = Integer.parseInt(timeParts[2]);
        this.initialSeconds = hour * 3600 + minute * 60 + second;
        this.remainingSeconds = this.initialSeconds;
        updateLabel();
    }

    public void start() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }
        if (timeline != null && timeline.getStatus() == Timeline.Status.PAUSED) {
            timeline.play();
            return;
        }
        if (remainingSeconds <= 0)
            return;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (remainingSeconds >= 1) {
                remainingSeconds--;
                updateLabel();

                // Mỗi 60 giây tăng sessionDuration lên 1 và lưu
                if ((initialSeconds - remainingSeconds) % 60 == 0) {
                    sessionDuration++;
                    if (studySessionController != null && userId > 0) {
                        studySessionController.logStudyTime(userId, 1); // lưu 1 phút mỗi phút
                    }
                }

                if (remainingSeconds <= 0) {
                    stop();
                    if (onFinishCallback != null) onFinishCallback.run();
                }
            } else {
                stop();
                if (onFinishCallback != null) onFinishCallback.run();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        timeline = null;

        if (sessionDuration > 0 && studySessionController != null && userId > 0) {
            studySessionController.logStudyTime(userId, sessionDuration);
            sessionDuration = 0;
        }

    }

    public void pause() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.pause();
        }
    }

    public void reset(String newTime) {
        stop();
        setTime(newTime);
    }

    public void reset() {
        reset(this.initialTimeString);
    }

    private void updateLabel() {
        if (timerLabel == null) return;
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("%02d : %02d : %02d", hours, minutes, seconds));
    }

    public boolean isRunning() {
        return timeline != null && timeline.getStatus() == Timeline.Status.RUNNING;
    }

    public boolean isPaused() {
        return timeline != null && timeline.getStatus() == Timeline.Status.PAUSED;
    }
}
