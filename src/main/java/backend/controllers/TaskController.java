package backend.controllers;

import backend.dao.TaskDAO;
import backend.models.Task;
import backend.models.TaskStat;

import java.util.List;

public class TaskController {
    private TaskDAO taskDAO;

    public TaskController() {
        taskDAO = new TaskDAO();
    }

    public boolean addTask(int userId, Boolean completed, String title) {
        Task task = new Task(title, completed, userId);
        return taskDAO.insert(task);
    }

    public boolean updateTask(int userId, Boolean completed, String title) {
        Task task = new Task(title, completed, userId);
        return taskDAO.update(task);
    }

    public boolean deleteTask(int taskId) {
        return taskDAO.delete(taskId);
    }

    public List<Task> getTasksByUser(int userId) {
        return taskDAO.findAllByUser(userId);
    }


    public List<TaskStat> getCompletedTaskStatsByMonth(int userId) {
        return taskDAO.getCompletedTaskStatsByMonth(userId);
    }

    public List<TaskStat> getCompletedTaskStatsByYear(int userId) {
        return taskDAO.getCompletedTaskStatsByYear(userId);
    }

    public List<TaskStat> getCompletedTaskStatsByDay(int userId) {
        return taskDAO.getCompletedTaskStatsByDay(userId);
    }
}
