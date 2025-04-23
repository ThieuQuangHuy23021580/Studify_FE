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
            System.out.println("Đang tải Sidebar.fxml...");
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/controller/FXML/Sidebar.fxml"));
            Node sidebarNode = sidebarLoader.load(); // Lấy Node
            SidebarController sidebarController = sidebarLoader.getController();
            if (sidebarController != null) {
                sidebarController.setMainController(this);
                mainView.setLeft(sidebarNode);
                System.out.println("Sidebar loaded and set.");
            } else {
                throw new IOException("Could not get SidebarController instance.");
            }
            loadAndSetCenterContent("/controller/FXML/DashBoard.fxml", null);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Lỗi tải giao diện", "Không thể tải các thành phần giao diện cần thiết: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Lỗi hệ thống", "Đã xảy ra lỗi khởi tạo không mong muốn: " + e.getMessage());
        }
    }

    /**
     * Được gọi từ LoginController để truyền User và khởi tạo lại view ban đầu nếu cần.
     */
    public void initData(User user) {
        if (user == null) {
            showErrorDialog("Lỗi Dữ Liệu", "Không nhận được thông tin người dùng hợp lệ.");
            return;
        }
        this.loggedInUser = user;
        loadAndSetCenterContent("/controller/FXML/DashBoard.fxml", this.loggedInUser);
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
                    ((DashBoardController) controller).initData(user);
                    System.out.println("Called initData for DashBoardController.");
                } else if (controller instanceof AIChatbotController) {
                    ((AIChatbotController) controller).initData(user);
                    System.out.println("Called initData for AIChatbotController.");
                } else if (controller instanceof TimeTableController) {
                    ((TimeTableController) controller).initData(user);
                    System.out.println("Called initData for TimeTableController.");
                } else if (controller instanceof StudyStatsController) {
                    ((StudyStatsController) controller).initData(user);
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


    /**
     * Tải và áp dụng background .
     */
    private void loadUserPreferences() {
        if (loggedInUser == null || backgroundDAO == null) {
            System.err.println("MainController Error: Cannot load background, user or DAO is null.");
            return;
        }
        if (!(currentCenterController instanceof DashBoardController)) {
            System.err.println("MainController WARN: Cannot set background because the current center view is not Dashboard.");
            return;
        }
        DashBoardController dashController = (DashBoardController) currentCenterController;
        int backgroundId = loggedInUser.getBackgroundId();
        Background userBackground = null;
        if (backgroundId > 0) {
            userBackground = backgroundDAO.getBackgroundById(backgroundId);
        }
        if (userBackground == null) {
            userBackground = backgroundDAO.getBackgroundById(1);
        }

        if (userBackground != null) {
            String imagePath = userBackground.getImagePath();
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                try (InputStream imageStream = getClass().getResourceAsStream(imagePath.trim())) {
                    if (imageStream == null) throw new NullPointerException("Resource not found: " + imagePath.trim());
                    Image bgImage = new Image(Objects.requireNonNull(getClass().getResource(imagePath).toExternalForm()));
                    dashController.setBackgroundImage(new ImageView(bgImage));

                } catch (Exception e) {
                    System.err.println("Error loading/setting background Image in MainController->loadUserPreferences: " + e.getMessage());
                    showErrorDialog("Lỗi Ảnh Nền", "Không thể tải hoặc đặt ảnh nền.");
                }
            } else {
                System.err.println("Image path is null or empty for background ID: " + userBackground.getId());
            }
        } else {
            System.err.println("ERROR: Could not load default background (ID 1) either!");
        }
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