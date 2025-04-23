package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.function.Consumer;

public class SingleSessionController {

    @FXML private AnchorPane sessionRootPane;
    @FXML private Label sessionLabel;
    private String sessionId;
    private Consumer<String> selectionHandler;

    /**
     * Đặt dữ liệu cho thẻ session.
     *
     * @param id      ID của session.
     * @param name    Tên hiển thị của session.
     * @param handler Hàm sẽ được gọi khi thẻ session được click, truyền vào sessionId.
     */
    public void setData(String id, String name, Consumer<String> handler) {
        this.sessionId = id;
        this.selectionHandler = handler;
        if (sessionLabel != null) {
            sessionLabel.setText(name);
        }
        if (sessionRootPane != null) {
            sessionRootPane.setOnMouseClicked(event -> {
                if (this.selectionHandler != null && this.sessionId != null) {
                    System.out.println("Session clicked: " + this.sessionId);
                    this.selectionHandler.accept(this.sessionId);
                }
            });
            sessionRootPane.setOnMouseEntered(e -> sessionRootPane.setStyle("-fx-background-color: #444; -fx-background-radius: 5;"));
            sessionRootPane.setOnMouseExited(e -> sessionRootPane.setStyle("-fx-background-color: transparent;"));
        } else {
            System.err.println("SingleSessionController Error: sessionRootPane is null.");
        }
    }

    public String getSessionId() {
        return sessionId;
    }
}