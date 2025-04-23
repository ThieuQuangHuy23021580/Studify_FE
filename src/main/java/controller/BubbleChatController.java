package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BubbleChatController {

    // *** QUAN TRỌNG: Đảm bảo fx:id này khớp với fx:id của Label trong cả 2 file FXML bubble ***
    // *** Nên đổi fx:id thành "messageLabel" trong cả AIBubbleChat.fxml và HumanBubbleChat.fxml ***
    @FXML
    private Label messageLabel; // Hoặc đổi tên thành messageLabel nếu bạn sửa FXML

    /**
     * Đặt nội dung text cho Label trong bubble chat.
     * @param text Nội dung tin nhắn.
     */
    public void setMessage(String text) {
        if (messageLabel != null) {
            messageLabel.setText(text);
            // Tự động xuống dòng nếu text dài
            messageLabel.setWrapText(true);
            // Có thể đặt Max Width ở đây hoặc trong FXML để wrap tốt hơn
            // aiMessageLabel.setMaxWidth(550); // Ví dụ
        } else {
            System.err.println("BubbleChatController Error: Label is null. Check fx:id in FXML.");
        }
    }
}