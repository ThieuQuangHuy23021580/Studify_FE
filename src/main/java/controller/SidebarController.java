package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
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

    public void handleDashboardClick() throws IOException {
        setSelected(dashboardBtn);
        loadMainContent("/controller/FXML/DashBoardView.fxml");
    }

    public void handleStudyStatsClick() throws IOException {
        setSelected(studystatsBtn);
        loadMainContent("/controller/FXML/DashBoardView.fxml");
    }

    public void handleTimetableClick() throws IOException {
        setSelected(timetableBtn);
        loadMainContent("/controller/FXML/DashBoardView.fxml");
    }

    public void handleMaterialClick() throws IOException {
        setSelected(studymaterialBtn);
        loadMainContent("/controller/FXML/DashBoardView.fxml");
    }

    public void handleAiChatbotClick() throws IOException {
        setSelected(aichatbotBtn);
        loadMainContent("/controller/FXML/DashBoardView.fxml");
    }

    private AnchorPane mainContent;

    @FXML
    private void initialize() {
        sidebarItems = List.of(dashboardBtn, studymaterialBtn, timetableBtn,studystatsBtn, aichatbotBtn);
    }

    /**
     *  Chọn Button trên Sidebar.
     * @param item là Button.
     */
    private void setSelected(VBox item){
        for(VBox box : sidebarItems){
            box.getStyleClass().remove("selected");
        }
        item.getStyleClass().add("selected");
        selectedItem = item;
    }

    /**
     * Gán mainContent ở Sidebar để show ở Main khi tương tác Button.
     * @param mainContent cần hiện trên màn hình.
     */
    public void setMainContent(AnchorPane mainContent) {
        this.mainContent = mainContent;
    }

    /**
     * Truyền mainContent từ Main vào Sidebar.
     * @param fxml resource từ Main.
     * @throws IOException ném ngoại lệ khi không load được fxml.
     */
    private void loadMainContent(String fxml) throws IOException {
        Parent content = FXMLLoader.load(getClass().getResource(fxml));
        mainContent.getChildren().setAll(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
    }






}
