package backend.controllers;

import backend.dao.ChatBotDAO;
import backend.models.ChatBot;

import java.util.List;
import java.util.Map;

public class ChatBotController {
    private ChatBot chatBot;
    private ChatBotDAO chatBotDAO;

    public ChatBotController() {
        this.chatBot = new ChatBot();
        this.chatBotDAO = new ChatBotDAO();
    }

    /**
     * Hỏi chatbot và nhận lại String lời phản hồi
     * @param sessionId ID của session
     * @param prompt câu hỏi muốn hỏi
     * */
    public String sendMessage(String sessionId, String prompt) {
        String response = chatBot.askChatbot(sessionId, prompt);
        return response;
    }


    public List<String> getConversationHistory(String sessionId) {
        return chatBot.getSessionHistory(sessionId);
    }
}