package controller;

import backend.dao.BackgroundDAO;
import backend.dao.UserDAO;
import backend.models.Background;
import backend.models.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainController {
    @FXML
    private BorderPane mainView;
    private User loggedInUser;
    private UserDAO userDAO;
    private BackgroundDAO backgroundDAO;
    private Object currentCenterController;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        backgroundDAO = new BackgroundDAO();
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/controller/FXML/Sidebar.fxml"));
            Node sidebarNode = sidebarLoader.load(); // Lấy Node
            SidebarController sidebarController = sidebarLoader.getController();
            if (sidebarController != null) {
                sidebarController.setMainController(this);
                mainView.setLeft(sidebarNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("MainController", "Không load được Sidebar.fxml: " + e.getMessage());
        }
    }

    /**
     * Được gọi từ LoginController để truyền User và khởi tạo lại view ban đầu nếu cần.
     */
    public void initData(User user) {
        if(user != null) {
            loggedInUser = user;
            loadAndSetCenterContent("/controller/FXML/DashBoard.fxml",loggedInUser);
        }
        else showErrorDialog("MainController", "Không lấy được user từ LoginView.");
    }

    /**
     * Phương thức chung để tải một FXML và đặt nó vào vùng center của mainView.
     *
     * @param fxmlPath Đường dẫn đến tệp FXML cần tải.
     * @param user     Đối tượng User để truyền vào initData.
     */
    public void loadAndSetCenterContent(String fxmlPath, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Objects.requireNonNull(fxmlPath, "FXML Path cannot be null")));
            Node contentNode = loader.load();
            Object controller = loader.getController();

            this.currentCenterController = controller;
            if (controller != null && user != null) {
                if (controller instanceof DashBoardController) {
                    System.out.println("Called initData for DashBoardController.");
                    ((DashBoardController) controller).initData(user);
                } else if (controller instanceof AIChatbotController) {
                    System.out.println("Called initData for AIChatbotController.");
                    ((AIChatbotController) controller).initData(user);
                } else if(controller instanceof TimeTableController){
                    System.out.println("Called initData for TimeTableController.");
                    ((TimeTableController) controller).initData(user);
                } else if(controller instanceof StudyStatsController){
                    System.out.println("Called initData for StudyStatsController.");
                    ((StudyStatsController)controller).initData(user);
                }
            }
            if (mainView != null) {
                mainView.setCenter(contentNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Lỗi Giao Diện", "Đã xảy ra lỗi khi chuyển đổi giao diện.");
        }
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}