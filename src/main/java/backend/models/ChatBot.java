package backend.models;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ChatBot {
    public String askChatbot(String prompt) {
        try {
            URL url = new URL("http://127.0.0.1:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String escapedPrompt = prompt
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

            return response;

        } catch (Exception e) {
            return "Lỗi khi gọi chatbot: " + e.getMessage();
        }
    }
}
