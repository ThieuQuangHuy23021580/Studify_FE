package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Node> contentCache = new HashMap<>();

    private StackPane mainContent;

    @FXML
    private void initialize() {
        sidebarItems = List.of(dashboardBtn, studymaterialBtn, timetableBtn,studystatsBtn, aichatbotBtn);
        setSelected(dashboardBtn);
    }

    public void setMainContent(Node mainContent) {
        this.mainContent = (StackPane) mainContent;
    }

    /**
     *  Chọn Button trên Sidebar.
     * @param item là Vbox chứa Button.
     */
    private void setSelected(VBox item){
        for(VBox box : sidebarItems){
            box.getStyleClass().remove("selected");
        }
        item.getStyleClass().add("selected");
        selectedItem = item;
    }
    /**
     * Truyền mainContent từ Main vào Sidebar.
     * @param fxmlPath resource từ Main.
     */
    private void loadMainContent(String fxmlPath) {
        try {
            Node newContent = contentCache.computeIfAbsent(fxmlPath, path -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                    return loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            mainContent.getChildren().setAll(newContent);

        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Failed to load main content" + e.getMessage());
        }
    }

    public void handleDashboardClick() throws IOException {
        setSelected(dashboardBtn);
        loadMainContent("/controller/FXML/DashBoard.fxml");
    }

    public void handleStudyStatsClick() throws IOException {
        setSelected(studystatsBtn);
        loadMainContent("/controller/FXML/StudyStats.fxml");
    }

    public void handleTimetableClick() throws IOException {
        setSelected(timetableBtn);
        loadMainContent("/controller/FXML/TimeTable.fxml");
    }

    public void handleMaterialClick() throws IOException {
        setSelected(studymaterialBtn);
        loadMainContent("/controller/FXML/StudyMaterial.fxml");
    }

    public void handleAiChatbotClick() throws IOException {
        setSelected(aichatbotBtn);
        loadMainContent("/controller/FXML/TestAIChatbot.fxml");
    }






}
