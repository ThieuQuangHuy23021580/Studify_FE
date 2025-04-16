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

    public String askBot(String prompt) {
        return chatBot.askChatbot(prompt);
    }

    public static void main(String[] args) {
        ChatBotController chatBotController = new ChatBotController();
        System.out.println(chatBotController.askBot("What is my name"));
    }
}