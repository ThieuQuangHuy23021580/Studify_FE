package controller;

import backend.controllers.ChatBotController;
import backend.controllers.SessionController;
import backend.models.Session;
import backend.models.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AIChatbotController {

    @FXML
    private FlowPane chatFlowPane;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button sendBtn;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private FlowPane sessionFlowPane;
    @FXML
    private Button newSessionBtn;
    @FXML
    private Button searchDataBtn;
    @FXML
    private Button deleteSessionBtn;

    private ChatBotController chatBotBackendController;
    private SessionController sessionBackendController;
    private User currentUser;
    private String currentSessionId;

    private Label typingIndicator;

    @FXML
    public void initialize() {
        System.out.println("AIChatbotController initializing...");
        chatBotBackendController = new ChatBotController();
        sessionBackendController = new SessionController();

        chatFlowPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (chatScrollPane != null) {
                Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
            }
        });
        if (sendTextField != null) {
            sendTextField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    sendBtnClicked();
                    event.consume();
                }
            });
        }
        updateChatInputState(false);
    }


    /**
     * Khởi tạo dữ liệu.
     */
    public void initData(User user) {
        this.currentUser = user;
        if (this.currentUser == null || this.currentUser.getUserId() == 0) {
            showAlert(Alert.AlertType.ERROR, "User Error:", "initData AIChatbotController thất bại.");
            if (sendBtn != null) sendBtn.setDisable(true);
            return;
        }
        loadSessionList();
        selectDefaultSession();
    }


    /**
     * Tải danh sách các session của người dùng hiện tại và hiển thị.
     */
    private void loadSessionList() {
        sessionFlowPane.getChildren().clear();
        String userIdStr = String.valueOf(currentUser.getUserId());
        List<Session> sessions = sessionBackendController.getSessionsForUser(userIdStr);

        if (sessions.isEmpty()) {
            Label noSessionsLabel = new Label("  No sessions yet.");
            noSessionsLabel.setStyle("-fx-text-fill: #a0a0a0;");
            sessionFlowPane.getChildren().addFirst(noSessionsLabel);
        } else {

            for (Session session : sessions) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/FXML/SingleSession.fxml"));
                    Node sessionNode = loader.load();
                    SingleSessionController controller = loader.getController();

                    if (controller != null) {
                        sessionNode.getProperties().put("controller", controller);
                        String displayName = session.getSessionId(); // Tạm thời dùng ID
                        //TODO: Tạo logic hiển thị đẹp hơn.
                        displayName = reformatChatDisplayName(displayName);
                        controller.setData(session.getSessionId(), displayName, this::handleSessionSelection);
                        sessionFlowPane.getChildren().add(sessionNode);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private String reformatChatDisplayName(String sessionId) {
        String firstMessage = chatBotBackendController.getFirstMessage(sessionId);
        System.out.println("reformat Displayname called :"+firstMessage);
        if(firstMessage != null && !firstMessage.trim().isEmpty()){
            int maxLength =  20;
            sessionId = firstMessage.length() > maxLength ? firstMessage.substring(0, maxLength) : firstMessage;
        }
        else return sessionId;
        return firstMessage;
    }

    /**
     * Chọn session đầu tiên (hoặc session được lưu trạng thái cuối cùng) làm mặc định.
     */
    private void selectDefaultSession() {
        if (sessionFlowPane != null && !sessionFlowPane.getChildren().isEmpty()) {
            Node firstNode = sessionFlowPane.getChildren().get(0);
            Object controllerObj = firstNode.getProperties().get("controller");
            if (controllerObj instanceof SingleSessionController) {
                handleSessionSelection(((SingleSessionController) controllerObj).getSessionId());
                return;
            }
        }
        currentSessionId = null;
        chatFlowPane.getChildren().clear();
        updateChatInputState(false);
    }

    /**
     * Được gọi khi một thẻ session trong sessionFlowPane được click.
     *
     * @param selectedSessionId ID của session được chọn.
     */
    private void handleSessionSelection(String selectedSessionId) {
        if (selectedSessionId == null || selectedSessionId.equals(this.currentSessionId)) {
            return;
        }
        this.currentSessionId = selectedSessionId;
        displayChatHistory(selectedSessionId);
        updateChatInputState(true);
        highlightSelectedSession(selectedSessionId);
    }

    /**
     * Tải lịch sử từ DAO và hiển thị các bubble chat cho session được chọn.
     *
     * @param sessionId ID của session cần hiển thị lịch sử.
     */
    private void displayChatHistory(String sessionId) {
        chatFlowPane.getChildren().clear();
        if (chatBotBackendController == null || sessionId == null) {
            System.err.println("Cannot display history: Backend controller or session ID is null.");
            updateChatInputState(false);
            return;
        }

        System.out.println("Displaying chat history for session: " + sessionId);

        // *** LẤY LỊCH SỬ TRỰC TIẾP TỪ DAO ***
        List<String> history = chatBotBackendController.getConversationHistory(sessionId);

        if (history.isEmpty()) {
            System.out.println("No messages found for this session.");
        } else {
            System.out.println("Rendering " + history.size() + " messages.");
            for (String messageLine : history) {
                String rolePrefixUser = "user: ";
                String rolePrefixBot = "bot: ";
                boolean isHuman;
                String messageContent;

                if (messageLine.startsWith(rolePrefixUser)) {
                    isHuman = true;
                    messageContent = messageLine.substring(rolePrefixUser.length()).trim();
                } else if (messageLine.startsWith(rolePrefixBot)) {
                    isHuman = false;
                    messageContent = messageLine.substring(rolePrefixBot.length()).trim();
                } else {
                    System.out.println("Skipping history line (unknown format): " + messageLine);
                    continue;
                }
                addAlignedMessageBubble(messageContent, isHuman);
            }
        }

        Platform.runLater(() -> {
            if (chatScrollPane != null) chatScrollPane.setVvalue(1.0);
        });
        updateChatInputState(true);
    }

    /**
     * Hightlight session đang được chọn trong danh sách.
     */
    private void highlightSelectedSession(String selectedId) {
        if (sessionFlowPane == null || selectedId == null) return;
        for (Node node : sessionFlowPane.getChildren()) {
            Object controllerObj = node.getProperties().get("controller");
            if (controllerObj instanceof SingleSessionController) {
                SingleSessionController ssc = (SingleSessionController) controllerObj;
                if (ssc.getSessionId().equals(selectedId)) {
                    node.setStyle("-fx-background-color: #556B2F; -fx-background-radius: 5;");
                } else {
                    node.setStyle("-fx-background-color: transparent;");
                }
            } else {
                node.setStyle("-fx-background-color: transparent;");
            }
        }
    }

    /**
     * Xử lý sự kiện khi nút "New Session" được nhấn.
     * Tạo ngay một session mới với ID duy nhất và tên "New Chat".
     */
    @FXML
    private void newSessionBtnClicked() {
        if (currentUser == null || sessionBackendController == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo session, thiếu thông tin người dùng.");
            return;
        }
        String newSessionId = "Chat_" + UUID.randomUUID().toString().substring(0, 8);
        String userIdStr = String.valueOf(currentUser.getUserId());
        System.out.println("Attempting to create new session with ID: " + newSessionId + " for User ID: " + userIdStr);
        boolean created = sessionBackendController.createNewSession(newSessionId, userIdStr);
        if (created) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/FXML/SingleSession.fxml"));
                Node sessionNode = loader.load();
                SingleSessionController controller = loader.getController();
                sessionNode.getProperties().put("controller", controller);

                controller.setData(newSessionId, "New Chat: " + newSessionId.substring(5), this::handleSessionSelection);
                if (!sessionFlowPane.getChildren().isEmpty() && !(sessionFlowPane.getChildren().get(0).getProperties().containsKey("controller"))) {
                    sessionFlowPane.getChildren().clear();
                }
                sessionFlowPane.getChildren().addFirst(sessionNode);
                handleSessionSelection(newSessionId);
            } catch (Exception e) {
                e.printStackTrace();
                loadSessionList();
                handleSessionSelection(newSessionId);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo session mới trong cơ sở dữ liệu.");
        }
    }

    /**
     * Gửi tin nhắn.
     */
    @FXML
    private void sendBtnClicked() {
        if (currentSessionId == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Session", "Vui lòng chọn hoặc tạo một session để bắt đầu chat.");
            return;
        }

        String userMessage = sendTextField.getText();
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return;
        }
        final String messageToSend = userMessage.trim();
        sendTextField.clear();
        addAlignedMessageBubble(messageToSend, true);

        new Thread(() -> {
            try {
                Platform.runLater(this::addTypingIndicator);
                String botResponse = chatBotBackendController.sendMessage(currentSessionId, messageToSend);
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addAlignedMessageBubble(botResponse, false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addAlignedMessageBubble("Xin lỗi, đã có lỗi xảy ra khi kết nối tới AI.", false);
                });
                System.err.println("Error sending message to chatbot backend: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Kích hoạt/Vô hiệu hóa input chat
     */
    private void updateChatInputState(boolean enabled) {
        if (sendTextField != null) sendTextField.setDisable(!enabled);
        if (sendBtn != null) sendBtn.setDisable(!enabled);
    }

    /**
     * Thêm bubble chat đã căn chỉnh
     */
    private void addAlignedMessageBubble(String message, boolean isHuman) {
        try {
            String fxmlPath = isHuman ? "/controller/FXML/HumanBubbleChat.fxml" : "/controller/FXML/AIBubbleChat.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            HBox bubbleContent = loader.load();

            Label msgLabel = (Label) bubbleContent.lookup("#messageLabel");
            if (msgLabel != null) {
                msgLabel.setText(message);
            } else {
                return;
            }

            HBox alignmentWrapper = new HBox();
            alignmentWrapper.setAlignment(isHuman ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            alignmentWrapper.getChildren().add(bubbleContent);

            Platform.runLater(() -> {
                if (chatFlowPane != null) {
                    chatFlowPane.getChildren().add(alignmentWrapper);
                    VBox.setMargin(alignmentWrapper, new javafx.geometry.Insets(3, 5, 3, 5));
                }
            });
        } catch (Exception e) {
            System.err.println("Error adding aligned message bubble: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Thêm chỉ báo đang gõ
     */
    private void addTypingIndicator() {
        if (typingIndicator == null) {
            typingIndicator = new Label("AI is thinking...");
            typingIndicator.setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
            HBox wrapper = new HBox(typingIndicator);
            wrapper.setAlignment(Pos.CENTER_LEFT);
            wrapper.setPadding(new javafx.geometry.Insets(0, 0, 0, 10)); // Thêm lề trái
            typingIndicator.setUserData("typing_indicator_wrapper");
            VBox.setMargin(wrapper, new javafx.geometry.Insets(3, 5, 3, 5));
        }
        removeTypingIndicator();
        if (!chatFlowPane.getChildren().contains(typingIndicator.getParent())) {
            Platform.runLater(() -> chatFlowPane.getChildren().add(typingIndicator.getParent()));
        }
    }

    /**
     * Xóa chỉ báo đang gõ
     */
    private void removeTypingIndicator() {
        Platform.runLater(() ->
                chatFlowPane.getChildren().removeIf(node -> "typing_indicator_wrapper".equals(node.getUserData()))
        );
    }

    /**
     * Hiển thị Alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            try {
                if (chatFlowPane != null && chatFlowPane.getScene() != null && chatFlowPane.getScene().getWindow() != null) {
                    alert.initOwner(chatFlowPane.getScene().getWindow());
                }
            } catch (Exception e) { /* Ignore */ }
            alert.showAndWait();
        });
    }
}