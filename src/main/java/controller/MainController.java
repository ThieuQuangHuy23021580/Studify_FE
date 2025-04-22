package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane mainView;

    @FXML
    public void initialize() {
        System.out.println("Bắt đầu khởi tạo MainController..."); // Debug log

        try {
            System.out.println("Đang tải Sidebar.fxml...");
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/controller/FXML/Sidebar.fxml"));
            StackPane sidebar = sidebarLoader.load();
            SidebarController sidebarController = sidebarLoader.getController();

            System.out.println("Đang tải DashBoard.fxml...");
            FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/controller/FXML/DashBoard.fxml"));
            StackPane dashboard = dashboardLoader.load();
            sidebarController.setMainContent(dashboard);


            mainView.setLeft(sidebar);
            mainView.setCenter(dashboard);

            System.out.println("Khởi tạo thành công!");
        } catch (IOException e) {
            System.err.println("LỖI khi tải FXML:");
            e.printStackTrace();
            showErrorDialog("Lỗi tải giao diện", "Không thể tải tệp FXML: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("LỖI không xác định:");
            e.printStackTrace();
            showErrorDialog("Lỗi hệ thống", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}