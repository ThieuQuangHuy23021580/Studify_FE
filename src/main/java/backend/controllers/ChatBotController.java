package backend.controllers;

import backend.dao.ChatBotDAO;
import backend.models.ChatBot;

import java.util.List;
import java.util.Map;

import backend.models.Schedule;
import controller.LoginController;
import org.json.JSONObject;

public class ChatBotController {
    private ChatBot chatBot;
    private ChatBotDAO chatBotDAO;
    ScheduleController scheduleController;

    public ChatBotController() {
        this.chatBot = new ChatBot();
        this.chatBotDAO = new ChatBotDAO();
        scheduleController = new ScheduleController();
    }

    /**
     * Hỏi chatbot và nhận lại String lời phản hồi
     * @param sessionId ID của session
     * @param prompt câu hỏi muốn hỏi
     * */
    public String sendMessage(String sessionId, String prompt) {
        String formattedPrompt = """
You are a smart personal study assistant. Based on the user's input, you must decide whether an action should be taken. If so, respond with a valid JSON in the format below.

Actions you can handle:
- add_schedule: Add a course to the study schedule
- check_study_stats: Check the user's study time or stats
- delete_schedules: Remove a course from the study schedule

Respond with a valid JSON format like this:

{
  "action": "<action_name>",
  "params": {
    ... appropriate key-value parameters ...
  },
  "message": "<a user-friendly message explaining the result>"
}

If the user is just asking a regular question or chatting (no action needed), respond like this:

{
  "action": "normal_chat",
  "params": {},
  "message": "<your reply to the question>"
}

Examples:

User: "Add a Math class on Monday at 2 PM"  
→  
{
  "action": "add_schedule",
  "params": {
    "course": "Math",
    "day": "Monday",
    "time": 14
  },
  "message": "✅ Math class has been added on Monday at 2 PM"
}

User: "Delete schedule on Monday at 2 PM"
→
{
  "action": "delete_schedule",
  "params": {
    "day": "Monday",
    "time": 14
  },
  "message": "🗑️ Removed schedule from Monday at 2 PM"
}

User: "How long have I studied today?"  
→  
{
  "action": "check_study_stats",
  "params": {},
  "message": "📊 You’ve studied for 3 hours and 15 minutes today."
}



Now, analyze the following user request and respond with a JSON in the same format:
"%s"
""".formatted(prompt);

        String rawResponse = chatBot.askChatbot(sessionId, formattedPrompt, prompt);
        SessionController sessionController = new SessionController();
        String userId = sessionController.getUserIdBySessionId(sessionId);

        try {
            JSONObject json = new JSONObject(rawResponse);

            String action = json.getString("action");
            JSONObject params = json.getJSONObject("params");
            String message = json.getString("message");

            switch (action) {
                case "add_schedule":
                    String course = params.getString("course");
                    String day = params.getString("day");
                    int time = params.getInt("time");

                    Schedule schedule = new Schedule(course, day, time, Integer.parseInt(userId));
                    scheduleController.addSchedule(schedule);
                    break;
                case "delete_schedule":
                    String dayToDelete = params.getString("day");
                    int timeToDelete = params.getInt("time");
                    scheduleController.deleteSchedule(dayToDelete, timeToDelete);
                    break;
                case "check_study_stats":
                    StudySessionController studyController = new StudySessionController();

                    int todayMinutes = studyController.getTotalStudyMinutes(Integer.parseInt(userId));
                    int currentStreak = studyController.getCurrentStreak(Integer.parseInt(userId));
                    int maxStreak = studyController.getMaxStreak(Integer.parseInt(userId));

                    String statMessage = "📊 You’ve studied for " + (todayMinutes / 60) + " hours and " + (todayMinutes % 60) + " minutes today. "
                            + "Your current streak is " + currentStreak + " days 🔥. "
                            + "Best streak: " + maxStreak + " days 💪.";

                    chatBotDAO.saveMessage(sessionId, "bot", statMessage);
                    return statMessage;
                case "normal_chat":
                    break;
                default:
                    return "❗ Unknown action from chatbot.";
            }

            chatBotDAO.saveMessage(sessionId, "bot", message);
            return message;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to parse chatbot response.";
        }
    }


    public List<String> getConversationHistory(String sessionId) {
        return chatBot.getSessionHistory(sessionId);
    }

    public String getFirstMessage(String sessionId) {
        return chatBot.getUserFirstMessage(sessionId);
    }
}