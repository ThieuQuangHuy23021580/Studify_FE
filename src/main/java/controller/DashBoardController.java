package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import kotlin.OverloadResolutionByLambdaReturnType;

import java.util.List;


public class DashBoardController {

    @FXML
    private Button addGoalBtn;

    @FXML
    private AnchorPane backgroundAnchorPane;

    @FXML
    private Label breakTimeFixLabel;

    @FXML
    private Button changeBackgroundBtn;

    @FXML
    private Button changeQuoteBtn;

    @FXML
    private Button changeSoundBtn;

    @FXML
    private Button closeBackgroundBtn;

    @FXML
    private Button closePomodoroFixBtn;

    @FXML
    private Button closePomodoroTimerBtn;

    @FXML
    private Button closeQuoteAnchorPaneBtn;

    @FXML
    private Button closeSessionGoalsBtn;

    @FXML
    private Button closeSoundAnchorPaneBtn;

    @FXML
    private Label focusTimeFixLabel;

    @FXML
    private Label focusTimeLabel;

    @FXML
    private Button fullScreenBtn;

    @FXML
    private RadioButton loopAutomaticcallyRadioBtn;

    @FXML
    private Button minusBreakTimeBtn;

    @FXML
    private Button minusFocusTimeBtn;

    @FXML
    private Button plusFocusTimeBtn;

    @FXML
    private Button plusTimeBreakBtn;

    @FXML
    private AnchorPane pomodoroAnchorPane;

    @FXML
    private AnchorPane pomodoroFixAnchorPane;

    @FXML
    private Button pomodoroFixBtn;

    @FXML
    private Button pomodoroTimerBtn;

    @FXML
    private AnchorPane quoteAnchorPane;

    @FXML
    private Text quoteLabel;

    @FXML
    private Button runPomodoroTimerBtn;

    @FXML
    private Button seeAllTaskBtn;

    @FXML
    private AnchorPane sessionGoalAnchorPane;

    @FXML
    private TextField sessionGoalTextField;

    @FXML
    private Button sessionGoalsBtn;

    @FXML
    private AnchorPane soundAnchorPane;

    @FXML
    private Button startTimerBtn;

    @FXML
    private AnchorPane taskAnchorPane;


    private List<AnchorPane> showStudyTool, showAllTool;

    private PomodoroTimerController pomodoroTimerController;

    @FXML
    public void initialize() {
        showStudyTool = List.of(quoteAnchorPane, soundAnchorPane, backgroundAnchorPane);
        showAllTool = List.of(pomodoroAnchorPane, pomodoroFixAnchorPane, sessionGoalAnchorPane, taskAnchorPane,
                quoteAnchorPane, soundAnchorPane, backgroundAnchorPane);
        for (AnchorPane anchorPane : showAllTool) showAnchorPane(anchorPane, false);
    }

    private void showAnchorPane(AnchorPane ap, boolean isShow) {
        ap.setVisible(isShow);
        ap.setManaged(isShow);
    }

    private void showAnchorPane(AnchorPane ap, List<AnchorPane> showStudyTool, boolean isShow) {
        for (AnchorPane anchorPane : showStudyTool) {
            showAnchorPane(anchorPane, false);
        }
        showAnchorPane(ap, true);
    }

    @FXML
    public void pomodoroTimerBtnClicked() {
        showAnchorPane(pomodoroAnchorPane, true);
    }

    @FXML
    public void sessionGoalsBtnClicked() {
        showAnchorPane(sessionGoalAnchorPane, true);
    }

    @FXML
    public void pomodoroFixBtnClicked() {
        showAnchorPane(pomodoroAnchorPane, false);
        showAnchorPane(pomodoroFixAnchorPane, true);
    }

    @FXML
    public void seeAllTaskBtnClicked() {
        showAnchorPane(taskAnchorPane, true);
    }

    @FXML
    public void changeBackgroundBtnClicked() {
        showAnchorPane(backgroundAnchorPane, showStudyTool, true);
    }

    @FXML
    public void changeSoundBtnClicked() {
        showAnchorPane(soundAnchorPane, showStudyTool, true);
    }

    @FXML
    public void changeQuoteBtnClicked() {
        showAnchorPane(quoteAnchorPane, showStudyTool, true);
    }

    @FXML
    public void closeSessionGoalsBtnClicked() {
        showAnchorPane(sessionGoalAnchorPane, false);
        showAnchorPane(taskAnchorPane, false);
    }

    @FXML
    public void closePomodoroTimerBtnClicked() {
        showAnchorPane(pomodoroAnchorPane, false);
    }

    @FXML
    public void closePomodoroFixBtnClicked() {
        showAnchorPane(pomodoroFixAnchorPane, false);
    }

    @FXML
    public void startTimerBtnClicked() {
        showAnchorPane(pomodoroFixAnchorPane, false);
        showAnchorPane(pomodoroAnchorPane, true);
    }

    @FXML
    public void closeBackgroundBtnClicked() {
        showAnchorPane(backgroundAnchorPane, false);
    }

    @FXML
    public void closeSoundAnchorPaneBtnClicked() {
        showAnchorPane(soundAnchorPane, false);
    }

    @FXML
    public void closeQuoteAnchorPaneBtnClicked() {
        showAnchorPane(quoteAnchorPane, false);
    }

    @FXML
    public void runPomodoroTimerBtnClicked() {
        if (pomodoroTimerController == null) {
            pomodoroTimerController = new PomodoroTimerController(focusTimeLabel);
        }
        pomodoroTimerController.start();
    }

    @FXML
    public void addGoalBtnClicked() {
    }

    @FXML
    public void minusFocusTimeBtnClicked() {
    }

    @FXML
    public void plusFocusTimeBtnClicked() {
    }

    @FXML
    public void minusBreakTimeBtnClicked() {
    }

    @FXML
    public void plusTimeBreakBtnClicked() {
    }

    @FXML
    public void fullScreenBtnClicked() {
    }

}
