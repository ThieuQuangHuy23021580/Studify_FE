package backend.controllers;

import backend.models.ChatBot;
import java.util.Scanner;

public class ChatBotController {
    private ChatBot chatBot;

    public ChatBotController() {
        chatBot = new ChatBot();
    }

    /**
     * Hỏi ChatBot và nhận được phản hồi kiểu String
     * @param prompt Câu hỏi
     * */

    public String askBot(String sessionId, String prompt) {
        return chatBot.askChatbot(sessionId, prompt);
    }
}