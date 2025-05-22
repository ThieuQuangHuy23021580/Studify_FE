package backend.models;

import backend.dao.ChatBotDAO;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

public class ChatBot {
    private Map<String, List<String>> sessionHistory;
    private ChatBotDAO chatBotDAO = null ;

    public ChatBot() {
        chatBotDAO = new ChatBotDAO();
        sessionHistory = chatBotDAO.loadAllSessionHistories();  
    }

    public String askChatbot(String sessionId, String prompt, String userMessage) {
        try {
            ChatBotDAO chatBotDAO = new ChatBotDAO(); // để lưu message

            List<String> history = sessionHistory.getOrDefault(sessionId, new ArrayList<>());

            history.add("user: " + prompt);
            if (history.size() > 20) {
                history = history.subList(history.size() - 20, history.size()); // giữ 20 dòng cuối
            }

            StringBuilder fullPrompt = new StringBuilder();
            for (String msg : history) {
                fullPrompt.append(msg).append("\n");
            }
            fullPrompt.append("bot:");

            // Dùng Gson để tạo JSON request
            JsonObject json = new JsonObject();
            json.addProperty("model", "llama3:8b");
            json.addProperty("prompt", fullPrompt.toString());
            json.addProperty("stream", false);

            URL url = new URL("http://127.0.0.1:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            String responseJson;
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                scanner.useDelimiter("\\A");
                responseJson = scanner.hasNext() ? scanner.next() : "";
            }

            conn.disconnect();

            JsonObject responseObj = JsonParser.parseString(responseJson).getAsJsonObject();
            String response = responseObj.get("response").getAsString().trim();

            history.add("bot: " + response);
            sessionHistory.put(sessionId, history);

            JSONObject newJson = new JSONObject(response);

            String message = newJson.getString("message");

            chatBotDAO.saveMessage(sessionId, "user", userMessage);
            //chatBotDAO.saveMessage(sessionId, "bot", message);

            return response;

        } catch (Exception e) {
            return "Lỗi khi gọi chatbot: " + e.getMessage();
        }
    }

    public List<String> getSessionHistory(String sessionId) {
        return sessionHistory.getOrDefault(sessionId, new ArrayList<>());
    }

    public String getUserFirstMessage(String sessionId) {
        return chatBotDAO.getFirstMessage(sessionId);
    }
}
