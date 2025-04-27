package controller;

import backend.controllers.StudySessionController;
import backend.models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StudyStatsController {
    private User currentUser;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private MenuItem weeklyBtn, monthlyBtn;

    @FXML
    private Label chartTitleLabel;

    @FXML
    private BarChart<String, Number> performanceChart;

    private StudySessionController studySessionBackendController;

    @FXML
    public void initialize() throws SQLException {
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
    }
    /**
     * Nhận dữ liệu User từ MainController.
     */
    public void initData(User user) {
        if(user == null) return;
        this.currentUser = user;
        loadAndDisplayChartData(ChartMode.WEEKLY);
    }

    private enum ChartMode {WEEKLY, MONTHLY}

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
                limit = 4;
                data = studySessionBackendController.getWeeklyStudyDurations(userId, limit);
                title = "Weekly Study Time (Last " + limit + " Weeks)";
                xAxisLabel = "Week";
                break;
            case MONTHLY:
                limit = 12;
                data = studySessionBackendController.getMonthlyStudyDurations(userId, limit);
                title = "Monthly Study Time (Last " + limit + " Months)";
                xAxisLabel = "Month (Year-Month)";
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

}

