package controller;

import javafx.event.ActionEvent; // Import ActionEvent
import javafx.fxml.FXML;
import javafx.scene.Node; // Import Node
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

public class TaskController {

    @FXML private Button deleteTaskBtn;
    @FXML private RadioButton doneTaskRadioBtn;
    @FXML private Label taskNameLabel;
    @FXML private AnchorPane taskRootNode;
    private FlowPane parentContainer;
    private DashBoardController dashBoardController;

    public void setData(String taskText, FlowPane container,DashBoardController dashBoardController) {
        this.taskNameLabel.setText(taskText);
        this.parentContainer = container;
        this.dashBoardController = dashBoardController;
    }

    @FXML
    void deleteTaskBtnClicked() {
        if (parentContainer != null && taskRootNode != null) {
            parentContainer.getChildren().remove(taskRootNode);
            this.dashBoardController.updateTaskCounts();
        } else {
            System.err.println("Lỗi: Không thể xóa task do thiếu parentContainer hoặc taskRootNode.");
        }
    }

    @FXML
    void doneTaskRadioBtnClicked(){
        this.dashBoardController.updateTaskCounts();
    }

    public boolean isCompeleted(){
        return doneTaskRadioBtn!= null &&doneTaskRadioBtn.isSelected();
    }



}