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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.stage.FileChooser;

import javax.print.attribute.standard.PageRanges;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StudyStatsController {
    private User currentUser;

    @FXML
    private Label emailLabel;

    @FXML
    private Button changeAvatarBtn;

    @FXML
    private ImageView shelfImageView;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private FlowPane mainFlowPane;

    @FXML
    private StackPane mainStackPane;

    @FXML
    private StackPane informationStackPane;

    @FXML
    private StackPane streakStackPane;

    @FXML
    private StackPane performanceStackPane;

    @FXML
    private StackPane contactStackPane;

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

    @FXML
    private Label miniUserNameLabel;

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

        mainFlowPane.prefWidthProperty().bind(mainScrollPane.widthProperty().subtract(20));
        mainStackPane.prefWidthProperty().bind(mainScrollPane.widthProperty().subtract(20));
        informationStackPane.prefWidthProperty().bind(mainScrollPane.widthProperty().subtract(20));
        streakStackPane.prefWidthProperty().bind(mainScrollPane.widthProperty().subtract(20));
        performanceStackPane.prefWidthProperty().bind(mainScrollPane.widthProperty().subtract(20));
        contactStackPane.prefWidthProperty().bind(mainScrollPane.widthProperty().subtract(20));
        shelfImageView.fitWidthProperty().bind(mainStackPane.widthProperty().subtract(10));

        Circle circle = new Circle(75);
        circle.setCenterX(75);
        circle.setCenterY(75);
        avatarImageView.setClip(circle);




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
        setUserAvatar();
        emailLabel.setText(currentUser.getEmail());
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
            miniUserNameLabel.setText(userNameLabel.getText());
            userDAO.setUserEducationLevel(currentUser.getUserId(), educationLevelString);
            educationLevelLabel.setText(educationLevelString);
            changeInfoBtn.setVisible(true);
            confirmBtn.setVisible(false);
            cancelBtn.setVisible(false);
            userNameField.setVisible(false);
            educationLevelMenuBtn.setVisible(false);
            Platform.runLater(() -> mainScrollPane.setVvalue(0.0));
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Username cannot be empty.", ButtonType.OK);
            alert.showAndWait();
            userNameField.requestFocus();
            Platform.runLater(() -> mainScrollPane.setVvalue(0.0));
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
        Platform.runLater(() -> mainScrollPane.setVvalue(0.0));
    }

    private void setLevel(){
        if (studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 50) {
            levelLabel.setText("(Beginner)");
            levelLabel.setStyle("-fx-text-fill: #2e96f7;");
        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 100) {
            levelLabel.setText("(Learner)");
            levelLabel.setStyle("-fx-text-fill: #2e96f7;");

        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 200){
            levelLabel.setText("(Intermediate)");
            levelLabel.setStyle("-fx-text-fill: #dd00ff; ");

        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 300) {
            levelLabel.setText("(Advanced)");
            levelLabel.setStyle("-fx-text-fill: #dd00ff;");

        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 400){
            levelLabel.setText("(Expert)");
            levelLabel.setStyle("-fx-text-fill: #f5652f;");
        }
        else if(studySessionBackendController.getTotalStudyHours(currentUser.getUserId())< 500) {
            levelLabel.setText("(Master)");
            levelLabel.setStyle("-fx-text-fill: #f5652f;");

        }
        else {
            levelLabel.setText("(Legend)");
            levelLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public void changeAvatarBtnClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(changeAvatarBtn.getScene().getWindow());
            if (selectedFile != null) {
                userDAO.setUserAvatar(currentUser.getUserId(), selectedFile.getAbsolutePath());
                try (FileInputStream fileInputStream = new FileInputStream(selectedFile)) {
                    Image image = new Image(fileInputStream);
                    avatarImageView.setImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }

    private void setUserAvatar(){
        System.out.println("setUserAvatar call");
        if(currentUser!=null){
            if(userDAO.getUserAvatar(currentUser.getUserId())!=null){
                String imagePath = userDAO.getUserAvatar(currentUser.getUserId());
                System.out.println(imagePath);
                Platform.runLater(() ->{
                    try(FileInputStream fileInputStream = new FileInputStream(imagePath.trim())){
                        Image image = new Image(fileInputStream);
                        avatarImageView.setImage(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}

