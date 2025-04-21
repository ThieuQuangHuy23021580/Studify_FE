package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;




public class DashBoardController {
    @FXML
    private AnchorPane pomodoroFixAnchorPane;
    @FXML
    private AnchorPane pomodoroAnchorPane;
    @FXML
    private AnchorPane sessionGoalAnchorPane;
    @FXML
    private AnchorPane taskAnchorPane;
    @FXML
    private AnchorPane backgroundAnchorPane;
    @FXML
    public void initialize() {
        pomodoroFixAnchorPane.setManaged(false);
        sessionGoalAnchorPane.setVisible(false);
        pomodoroAnchorPane.setVisible(false);
        taskAnchorPane.setVisible(false);
        backgroundAnchorPane.setVisible(false);


    }
}
