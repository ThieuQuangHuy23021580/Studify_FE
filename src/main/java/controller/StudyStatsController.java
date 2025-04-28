package controller;

import backend.controllers.StudySessionController;
import backend.dao.UserDAO;
import backend.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javax.print.attribute.standard.PageRanges;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StudyStatsController {
    private User currentUser;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label averageTimeLabel;

    @FXML
    private Button changeInfoBtn;

    @FXML
    private Label chartTitleLabel;

    @FXML
    private Label currentStreakLabel;

    @FXML
    private Label educationLevelLabel;

    @FXML
    private MenuItem highschoolMenuItem;

    @FXML
    private Label levelLabel;

    @FXML
    private Label longgestStreakLabel;

    @FXML
    private MenuItem monthlyBtn;

    @FXML
    private BarChart<String, Number> performanceChart;

    @FXML
    private MenuItem primaryMenuItem;

    @FXML
    private MenuItem secondaryMenuItem;

    @FXML
    private Label studyTimeLabel;

    @FXML
    private MenuItem tertiaryMenuItem;

    @FXML
    private MenuButton timePeriodMenuBtn;

    @FXML
    private TextField userNameField;

    @FXML
    private Label userNameLabel;

    @FXML
    private MenuItem vocationalMenuItem;

    @FXML
    private MenuItem weeklyBtn;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private MenuItem yearlyBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private MenuButton educationLevelMenuBtn;

    private StudySessionController studySessionBackendController;

    private UserDAO userDAO;

    private String educationLevelString;

    @FXML
    public void initialize() throws SQLException {
        educationLevelString = educationLevelLabel.getText();
        userDAO = new UserDAO();
        studySessionBackendController = new StudySessionController();
        if (performanceChart != null) {
            performanceChart.setAnimated(true);
            performanceChart.setLegendVisible(false);
        }
        if (xAxis != null) {
            xAxis.setLabel("Time Period");
        }
        if (yAxis != null) {
            yAxis.setLabel("Study Duration (Minutes)");
            yAxis.setForceZeroInRange(true);
        }
        if(weeklyBtn != null) weeklyBtn.setOnAction(e -> loadAndDisplayChartData(ChartMode.WEEKLY));
        if(monthlyBtn != null) monthlyBtn.setOnAction(e -> loadAndDisplayChartData(ChartMode.MONTHLY));
        if(yearlyBtn != null) yearlyBtn.setOnAction(e -> loadAndDisplayChartData(ChartMode.YEARLY));
        if(secondaryMenuItem != null) secondaryMenuItem.setOnAction(e -> { educationLevelString = "Secondary"; });
        if(highschoolMenuItem!=null) highschoolMenuItem.setOnAction(e -> { educationLevelString = "Highschool"; });
        if(primaryMenuItem != null) primaryMenuItem.setOnAction(e -> { educationLevelString = "Primary"; });
        if(vocationalMenuItem !=null) vocationalMenuItem.setOnAction(e -> { educationLevelString = "Vocational"; });
        if(tertiaryMenuItem != null) tertiaryMenuItem.setOnAction(e -> { educationLevelString = "Tertiary"; });

    }
    /**
     * Nhận dữ liệu User từ MainController.
     */
    public void initData(User user) {
        if(user == null) return;
        this.currentUser = user;
        loadAndDisplayChartData(ChartMode.WEEKLY);
        studyTimeLabel.setText(String.valueOf(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())));
        averageTimeLabel.setText(String.valueOf(studySessionBackendController.getAverageStudyHours(currentUser.getUserId())));
        currentStreakLabel.setText(String.valueOf(studySessionBackendController.getCurrentStreak(currentUser.getUserId())));
        longgestStreakLabel.setText(String.valueOf(studySessionBackendController.getMaxStreak(currentUser.getUserId())));
        userNameLabel.setText(userDAO.getUserName(currentUser.getUserId()));
        educationLevelLabel.setText(userDAO.getUserEducationLevel(currentUser.getUserId()));
        setLevel();
    }

    private enum ChartMode {WEEKLY, MONTHLY, YEARLY}

    /**
     * Tải dữ liệu từ backend dựa trên chế độ xem và hiển thị lên biểu đồ.
     */
    private void loadAndDisplayChartData(ChartMode mode) {
        if (currentUser == null || studySessionBackendController == null || performanceChart == null) {
            return;
        }
        int userId = currentUser.getUserId();
        Map<String, Integer> data = null;
        String title = "Study Performance";
        String xAxisLabel = "Period";
        int limit = 12;

        switch (mode) {
            case WEEKLY:
                limit = 7;
                data = studySessionBackendController.getDailyStudyDurations(userId, limit);
                title = "Weekly Study Time (Last " + limit + " Days)";
                xAxisLabel = null;
                break;
            case MONTHLY:
                limit = 30;
                data = studySessionBackendController.getDailyStudyDurations(userId, limit);
                title = "Monthly Study Time (Last " + limit + " Days)";
                xAxisLabel = null;
                break;
            case YEARLY:
                limit = 12;
                data = studySessionBackendController.getMonthlyStudyDurations(userId, limit);
                title = "Yearly Study Time (Last " + limit + " Months)";
                xAxisLabel = null;
                break;
        }

        if(chartTitleLabel != null) chartTitleLabel.setText(title); else performanceChart.setTitle(title);
        if(xAxis != null) xAxis.setLabel(xAxisLabel);

        populateChart(data);
    }

    /**
     * Cập nhật BarChart với dữ liệu được cung cấp.
     * @param data Map chứa dữ liệu (Key: Nhãn trục X, Value: Giá trị trục Y).
     */
    private void populateChart(Map<String, Integer> data) {
        if (performanceChart == null || data == null) {
            System.err.println("Cannot populate chart: Chart component or data is null.");
            return;
        }
        System.out.println("Populating chart with " + data.size() + " data points.");
        Platform.runLater(() -> {
            performanceChart.getData().clear();
            if (data.isEmpty()) {
                System.out.println("No data to display on chart.");
                return;
            }
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Minutes");

            List<Map.Entry<String, Integer>> entries = new ArrayList<>(data.entrySet());
            Collections.reverse(entries);

            ObservableList<String> categories = FXCollections.observableArrayList();

            for (Map.Entry<String, Integer> entry : entries) {
                String category = entry.getKey();
                Number value = entry.getValue();
                categories.add(category);
                series.getData().add(new XYChart.Data<>(category, value));
                System.out.println("Chart data point: " + category + " = " + value);
            }
            if (xAxis != null) {
                xAxis.setCategories(categories);
            } else {
                System.err.println("Cannot set categories, xAxis is null.");
            }
            performanceChart.getData().add(series);
        });
    }

    public void changeInfoBtnClicked() {
        userNameField.clear();
        userNameField.setVisible(true);
        changeInfoBtn.setVisible(false);
        confirmBtn.setVisible(true);
        cancelBtn.setVisible(true);
        educationLevelMenuBtn.setVisible(true);
    }

    public void confirmBtnClicked() {
        if(!userNameField.getText().isEmpty()) {
            userDAO.setUserName(currentUser.getUserId(), userNameField.getText());
            userNameLabel.setText(userDAO.getUserName(currentUser.getUserId()));
            userDAO.setUserEducationLevel(currentUser.getUserId(), educationLevelString);
            educationLevelLabel.setText(educationLevelString);
            changeInfoBtn.setVisible(true);
            confirmBtn.setVisible(false);
            cancelBtn.setVisible(false);
            userNameField.setVisible(false);
            educationLevelMenuBtn.setVisible(false);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Username cannot be empty.", ButtonType.OK);
            alert.showAndWait();
            userNameField.requestFocus();
        }
    }

    public void cancelBtnClicked(){
        userNameField.setVisible(false);
        changeInfoBtn.setVisible(true);
        confirmBtn.setVisible(false);
        educationLevelMenuBtn.setVisible(false);
        educationLevelString = educationLevelLabel.getText();
        cancelBtn.setVisible(false);
        educationLevelMenuBtn.setVisible(false);
    }

    private void setLevel(){
        if (studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 50) {
            levelLabel.setText("(Beginner)");
            levelLabel.setStyle("-fx-text-fill: #2e96f7;-fx-font-weight: bold; -fx-font-size: 21;");
        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 100) {
            levelLabel.setText("(Learner)");
            levelLabel.setStyle("-fx-text-fill: #2e96f7; -fx-font-weight: bold; -fx-font-size: 21;");

        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 200){
            levelLabel.setText("(Intermediate)");
            levelLabel.setStyle("-fx-text-fill: #dd00ff;-fx-font-weight: bold; -fx-font-size: 21;");

        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 300) {
            levelLabel.setText("(Advanced)");
            levelLabel.setStyle("-fx-text-fill: #dd00ff;-fx-font-weight: bold; -fx-font-size: 21;");

        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 400){
            levelLabel.setText("(Expert)");
            levelLabel.setStyle("-fx-text-fill: #f5652f;-fx-font-weight: bold; -fx-font-size: 21;");
        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 500) {
            levelLabel.setText("(Master)");
            levelLabel.setStyle("-fx-text-fill: #f5652f;-fx-font-weight: bold; -fx-font-size: 21;");

        }
        else {
            levelLabel.setText("(Legend)");
            levelLabel.setStyle("-fx-text-fill: red;-fx-font-weight: bold; -fx-font-size: 21;");
        }
    }

}

