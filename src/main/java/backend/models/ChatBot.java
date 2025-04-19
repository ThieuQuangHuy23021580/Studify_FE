package backend.models;

import backend.dao.ChatBotDAO;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ChatBot {
    private Map<String, List<String>> sessionHistory;

    public ChatBot() {
        ChatBotDAO chatBotDAO = new ChatBotDAO();
        sessionHistory = chatBotDAO.loadAllSessionHistories();  
    }

    public String askChatbot(String sessionId, String prompt) {
        try {
            List<String> history = sessionHistory.getOrDefault(sessionId, new ArrayList<>());

            history.add("User: " + prompt);

            StringBuilder fullPrompt = new StringBuilder();
            for (String msg : history) {
                fullPrompt.append(msg).append("\n");
            }
            fullPrompt.append("Bot:");

            String escapedPrompt = fullPrompt.toString()
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

            URL url = new URL("http://127.0.0.1:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

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

            history.add("Bot: " + response);
            sessionHistory.put(sessionId, history);

            return response;

        } catch (Exception e) {
            return "Lỗi khi gọi chatbot: " + e.getMessage();
        }
    }

    public List<String> getSessionHistory(String sessionId) {
        return sessionHistory.getOrDefault(sessionId, new ArrayList<>());
    }

    public static void main(String[] args) {
        ChatBot bot = new ChatBot();
        String sessionId = "user123";

        System.out.println("Bot 1: " + bot.askChatbot(sessionId, "My favorite color is blue"));
        System.out.println("Bot 2: " + bot.askChatbot(sessionId, "What is my favorite color?"));

        System.out.println("\n--- Chat History ---");
        for (String msg : bot.getSessionHistory(sessionId)) {
            System.out.println(msg);
        }
    }

}
