package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;




public class DashBoardController {
    @FXML
    private AnchorPane pomodoroFixAnchorPane;
    @FXML
    private AnchorPane pomodoroAnchorPane;
    @FXML
    public void initialize() {
        pomodoroFixAnchorPane.setManaged(false);
    }
}
