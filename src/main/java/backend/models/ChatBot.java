package backend.models;

import backend.controllers.ChatBotController;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChatBot {
    private Map<String, String> sessionHistory = new HashMap<>();

    public String askChatbot(String sessionId, String prompt) {
        try {
            URL url = new URL("http://127.0.0.1:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String history = sessionHistory.getOrDefault(sessionId, "");
            String updatedPrompt = history + "\nUser: " + prompt + "\nBot:";

            String escapedPrompt = updatedPrompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            String jsonInput = """
            {
              "model": "llama3:8b",
              "prompt": "%s",
              "stream": false
            }
            """.formatted(escapedPrompt);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            String responseJson;
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                scanner.useDelimiter("\\A");
                responseJson = scanner.hasNext() ? scanner.next() : "";
            }

            conn.disconnect();

            int start = responseJson.indexOf("\"response\":\"") + 12;
            int end = responseJson.indexOf("\"", start);
            String response = responseJson.substring(start, end).replace("\\n", "\n");

            sessionHistory.put(sessionId, updatedPrompt + response);

            return response;

        } catch (Exception e) {
            return "Lỗi khi gọi chatbot: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        ChatBot bot = new ChatBot();
        String sessionId = "user123";

        String r1 = bot.askChatbot(sessionId, "I'm 20 years old. Please remember it");
        System.out.println("Bot 1: " + r1);

        String r2 = bot.askChatbot(sessionId, "What did I just ask you to do");
        System.out.println("Bot 2: " + r2);

        String r3 = bot.askChatbot(sessionId, "What's my age?");
        System.out.println("Bot 3: " + r3);
    }
}
