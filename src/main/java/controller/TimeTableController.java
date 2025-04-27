package controller;

import backend.controllers.ScheduleController;
import backend.models.Schedule;
import backend.models.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit; // Giữ lại import này nếu bạn dùng trong các hàm khác

public class TimeTableController {

    @FXML
    private GridPane timeTableGridPane;

    @FXML
    private TextField courseNameTextField;

    @FXML
    private TextField timeTextField;

    @FXML
    private Button addBtn;

    @FXML
    private Button deleteBtn;

    private final Map<String, Integer> dayToColumnMap = new HashMap<>();

    private static final int MIN_COURSE_ROW_INPUT = 7;
    private static final int MAX_COURSE_ROW_INPUT = 20;
    private static final int GRID_ROW_OFFSET = 1;
    private static final int MIN_COURSE_COLUMN_INDEX = 1;
    private static final int MAX_COURSE_COLUMN_INDEX = 7;
    private User user;

    private ScheduleController scheduleController;
    private List<Schedule> schedules;

    @FXML
    public void initialize() {
        dayToColumnMap.put("MONDAY", 1);
        dayToColumnMap.put("TUESDAY", 2);
        dayToColumnMap.put("WEDNESDAY", 3);
        dayToColumnMap.put("THURSDAY", 4);
        dayToColumnMap.put("FRIDAY", 5);
        dayToColumnMap.put("SATURDAY", 6);
        dayToColumnMap.put("SUNDAY", 7);
        scheduleController = new ScheduleController();
    }

    @FXML
    private void addBtnClicked() {
        String courseName = courseNameTextField.getText();
        String timeInput = timeTextField.getText();

        if (courseName == null || courseName.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập tên khóa học.");
            courseNameTextField.requestFocus();
            return;
        }
        if (timeInput == null || timeInput.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập thời gian (ví dụ: 7-Monday).");
            timeTextField.requestFocus();
            return;
        }

        GridCoordinates coords = parseAndValidateCoordinates(timeInput);
        if (coords == null) {
            timeTextField.requestFocus();
            return;
        }

        String[] parts = timeInput.trim().split("-");
        int hour = Integer.parseInt(parts[0].trim());
        String day = parts[1].trim();

        if (user != null) {
            Schedule newSchedule = new Schedule(courseName.trim(), day, hour, user.getUserId());
            scheduleController.addSchedule(newSchedule);
        }

        // Hiển thị trên UI
        Label targetLabel = findLabelAt(coords.colIndex, coords.rowIndex);
        if (targetLabel != null) {
            targetLabel.setText(courseName.trim());
            targetLabel.setStyle("-fx-text-fill:#E8B931; -fx-border-color:white; -fx-background-color: black; -fx-padding: 2px; -fx-alignment: center;");
            courseNameTextField.clear();
            timeTextField.clear();
        }
    }

    @FXML
    private void deleteBtnClicked() {
        String timeInput = timeTextField.getText();

        if (timeInput == null || timeInput.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập thời gian cần xóa (ví dụ: 7-Monday).");
            timeTextField.requestFocus();
            return;
        }

        GridCoordinates coords = parseAndValidateCoordinates(timeInput);
        if (coords == null) {
            timeTextField.requestFocus();
            return;
        }
        Label targetLabel = findLabelAt(coords.colIndex, coords.rowIndex);

        if (targetLabel != null) {
            if (targetLabel.getText() != null && !targetLabel.getText().trim().isEmpty()) {
                targetLabel.setText("");
                targetLabel.setStyle("-fx-text-fill:white; -fx-background-color:transparent;-fx-border-color:white; -fx-padding: 2px; -fx-alignment: center");
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa nội dung khóa học khỏi thời gian biểu.");
                timeTextField.clear();
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Không có nội dung khóa học tại vị trí đã chỉ định để xóa.");
            }
        }
    }


    private GridCoordinates parseAndValidateCoordinates(String timeInput) {
        String[] parts = timeInput.trim().split("-");
        if (parts.length != 2) {
            showAlert(Alert.AlertType.ERROR, "Định dạng sai", "Định dạng thời gian phải là 'Giờ-Ngày' (ví dụ: 7-Monday).");
            return null;
        }
        String rowStr = parts[0].trim();
        String dayStr = parts[1].trim().toUpperCase();
        int inputRow;
        int colIndex;
        int rowIndex;
        try {
            inputRow = Integer.parseInt(rowStr);
            if (inputRow < MIN_COURSE_ROW_INPUT || inputRow > MAX_COURSE_ROW_INPUT) {
                showAlert(Alert.AlertType.ERROR, "Giờ không hợp lệ", "Giờ phải nằm trong khoảng từ " + MIN_COURSE_ROW_INPUT + " đến " + MAX_COURSE_ROW_INPUT + ".");
                return null;
            }
            rowIndex = inputRow - MIN_COURSE_ROW_INPUT + GRID_ROW_OFFSET;
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Định dạng sai", "Phần giờ '" + rowStr + "' phải là một con số.");
            return null;
        }
        if (!dayToColumnMap.containsKey(dayStr)) {
            showAlert(Alert.AlertType.ERROR, "Ngày không hợp lệ", "Ngày phải là một trong các giá trị: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday.");
            return null;
        }
        colIndex = dayToColumnMap.get(dayStr);
        if (colIndex < MIN_COURSE_COLUMN_INDEX || colIndex > MAX_COURSE_COLUMN_INDEX || rowIndex < GRID_ROW_OFFSET || rowIndex > (MAX_COURSE_ROW_INPUT - MIN_COURSE_ROW_INPUT + GRID_ROW_OFFSET) ) {
            System.err.println("Lỗi logic: Tọa độ tính toán nằm ngoài phạm vi cho phép!");
            showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", "Tọa độ tính toán không hợp lệ.");
            return null;
        }
        return new GridCoordinates(colIndex, rowIndex);

    }

    private Node findNodeAt(int colIndex, int rowIndex) {
        for (Node node : timeTableGridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);

            int c = (nodeCol == null) ? 0 : nodeCol;
            int r = (nodeRow == null) ? 0 : nodeRow;

            if (c == colIndex && r == rowIndex) {
                return node;
            }
        }
        return null;
    }


    private Label findLabelAt(int colIndex, int rowIndex) {
        Node foundNode = findNodeAt(colIndex, rowIndex);
        if (foundNode instanceof Label) {
            return (Label) foundNode;
        }
        return null;
    }

    public void initData(User user) {
        if(user != null) {
            this.user = user;
            loadSchedule();
        }
    }

    private static class GridCoordinates {
        final int colIndex;
        final int rowIndex;
        GridCoordinates(int colIndex, int rowIndex) {
            this.colIndex = colIndex;
            this.rowIndex = rowIndex;
        }
    }

    public void loadSchedule() {
        if (user == null) return;
        System.out.println(user.getUserId());
        schedules = scheduleController.getSchedulesByUserId(user.getUserId());
        for (Schedule schedule : schedules) {
            String day = schedule.getDay().toUpperCase();
            int hour = schedule.getPeriod();

            int colIndex = dayToColumnMap.getOrDefault(day, -1);
            int rowIndex = hour - MIN_COURSE_ROW_INPUT + GRID_ROW_OFFSET;

            if (colIndex != -1 && rowIndex >= GRID_ROW_OFFSET) {
                Label label = findLabelAt(colIndex, rowIndex);
                if (label != null) {
                    label.setText(schedule.getCourseName());
                    label.setStyle("-fx-text-fill:#E8B931; -fx-border-color:white; -fx-background-color: black; -fx-padding: 2px; -fx-alignment: center;");
                }
            }
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            try {
                if(timeTableGridPane != null && timeTableGridPane.getScene() != null && timeTableGridPane.getScene().getWindow() != null) {
                    alert.initOwner(timeTableGridPane.getScene().getWindow());
                }
            } catch (Exception e) { /* Ignore */ }
            alert.showAndWait();
        });

    }

}