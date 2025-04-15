package com.example.chat;
import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;

public class GeminiClient {
    private static final String API_KEY = "AIzaSyBdC7-qzj9Of1T8Z00F4VOsYVZZ_8EP4Dc"; // API key gemini
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public String chat(String userMessage) throws IOException {
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", userMessage);

        JsonArray parts = new JsonArray();
        parts.add(textPart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", contents);

        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseStr = response.body().string();
            System.out.println("API RESPONSE RAW: " + responseStr); // In ra phản hồi thô

            if (!response.isSuccessful()) {
                System.out.println("API request failed with code: " + response.code());
                return "⚠️ Lỗi khi gọi API.";
            }

            JsonObject responseJson = JsonParser.parseString(responseStr).getAsJsonObject();
            JsonArray candidates = responseJson.getAsJsonArray("candidates");

            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonArray partsArray = firstCandidate.getAsJsonObject("content").getAsJsonArray("parts");
                return partsArray.get(0).getAsJsonObject().get("text").getAsString();
            } else {
                return "⚠️ Không có phản hồi từ Gemini.";
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        GeminiClient gemini = new GeminiClient();

        System.out.println("Chat với Gemini (gõ 'exit' để thoát):");

        while (true) {
            System.out.print("Bạn: ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;

            String response = gemini.chat(input);
            System.out.println("Gemini: " + response);
        }
    }
}
