package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BubbleChatController {
    @FXML
    private Label messageLabel;

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}
