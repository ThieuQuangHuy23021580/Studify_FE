package controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class PomodoroTimerController {
    private Label timerLabel;
    private Label originalTimerLabel;
    private String[] time;
    private Timeline timeline;

    int hour;
    int minute;
    int second;
    int remainingSeconds;

    public PomodoroTimerController(Label timerLabel) {
        this.timerLabel = timerLabel;
        time = timerLabel.getText().split(":");
        hour = Integer.parseInt(time[0]);
        minute = Integer.parseInt(time[1]);
        second = Integer.parseInt(time[2]);
        remainingSeconds = hour * 3600 + minute * 60 + second;
        originalTimerLabel = new Label();
        originalTimerLabel.setText(timerLabel.getText());
    }

    public void start() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (remainingSeconds >= 1) remainingSeconds--;
            updateLabel();
            if (remainingSeconds <= 0) {
                reset();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public void reset() {
        stop();
        timerLabel.setText(originalTimerLabel.getText());
        time = originalTimerLabel.getText().split(":");
        hour = Integer.parseInt(time[0]);
        minute = Integer.parseInt(time[1]);
        second = Integer.parseInt(time[2]);
        remainingSeconds = hour * 3600 + minute * 60 + second;
        updateLabel();
    }

    private void updateLabel() {
        int hour = remainingSeconds / 3600;
        int minute = (remainingSeconds - (hour * 3600)) / 60;
        int second = remainingSeconds - hour * 3600 - minute * 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", hour, minute, second));

    }


}
