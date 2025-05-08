package controller;

import backend.models.User;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.List;

public class SidebarController {

    @FXML
    private VBox aichatbotBtn;
    @FXML
    private VBox dashboardBtn;
    @FXML
    private VBox sidebarContainer;
    @FXML
    private VBox studymaterialBtn;
    @FXML
    private VBox studystatsBtn;
    @FXML
    private VBox timetableBtn;

    private List<VBox> sidebarItems;
    private VBox selectedItem;
    private MainController mainController;

    @FXML
    private void initialize() {
        sidebarItems = List.of(dashboardBtn, timetableBtn, studystatsBtn, aichatbotBtn);
        setSelected(dashboardBtn);
        System.out.println("SidebarController initialized.");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Đánh dấu mục được chọn trên Sidebar (thay đổi giao diện).
     *
     * @param item VBox của mục được chọn.
     */
    private void setSelected(VBox item) {
        if (sidebarItems == null) return;
        for (VBox box : sidebarItems) {
            if (box != null) {
                box.getStyleClass().remove("selected");
            }
        }
        if (item != null) {
            item.getStyleClass().add("selected");
            selectedItem = item;
        }
    }

    /**
     * Xử lý sự kiện khi nhấn vào nút Dashboard.
     */
    @FXML
    public void handleDashboardClick() {
        setSelected(dashboardBtn);
        if (mainController != null) {
            User currentUser = mainController.getLoggedInUser();
            mainController.loadAndSetCenterContent("/controller/FXML/DashBoard.fxml", currentUser);
        } else {
            System.err.println("Sidebar Error: MainController is null, cannot load Dashboard view.");
        }
    }

    /**
     * Xử lý sự kiện khi nhấn vào nút Study Stats.
     */
    @FXML
    public void handleStudyStatsClick() {
        setSelected(studystatsBtn);
        if (mainController != null) {
            User currentUser = mainController.getLoggedInUser();
            mainController.loadAndSetCenterContent("/controller/FXML/NewStudyStats.fxml", currentUser); // Giả sử StudyStats cũng cần User
        } else {
            System.err.println("Sidebar Error: MainController is null, cannot load Study Stats view.");
        }
    }

    /**
     * Xử lý sự kiện khi nhấn vào nút TimeTable.
     */
    @FXML
    public void handleTimetableClick() {
        setSelected(timetableBtn);
        if (mainController != null) {
            User currentUser = mainController.getLoggedInUser();
            mainController.loadAndSetCenterContent("/controller/FXML/TimeTable.fxml", currentUser); // Giả sử TimeTable cũng cần User
        } else {
            System.err.println("Sidebar Error: MainController is null, cannot load TimeTable view.");
        }
    }

//    /**
//     * Xử lý sự kiện khi nhấn vào nút Study Material.
//     */
//    @FXML
//    public void handleMaterialClick() {
//        setSelected(studymaterialBtn);
//        if (mainController != null) {
//            User currentUser = mainController.getLoggedInUser();
//            mainController.loadAndSetCenterContent("/controller/FXML/StudyMaterial.fxml", currentUser); // Giả sử Material cũng cần User
//        } else {
//            System.err.println("Sidebar Error: MainController is null, cannot load Study Material view.");
//        }
//    }

    /**
     * Xử lý sự kiện khi nhấn vào nút AI Chatbot.
     */
    @FXML
    public void handleAiChatbotClick() {
        setSelected(aichatbotBtn);
        if (mainController != null) {
            User currentUser = mainController.getLoggedInUser();
            if (currentUser != null) {
                mainController.loadAndSetCenterContent("/controller/FXML/AIChatbot.fxml", currentUser);
            } else {
                System.err.println("Sidebar Error: User not logged in, cannot navigate to Chatbot.");
            }
        } else {
            System.err.println("Sidebar Error: MainController is null, cannot load AI Chatbot view.");
        }
    }
}