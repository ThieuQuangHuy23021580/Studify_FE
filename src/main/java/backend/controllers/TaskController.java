package backend.controllers;

import backend.dao.TaskDAO;
import backend.models.Task;

import java.util.List;

public class TaskController {
    private TaskDAO taskDAO;

    public TaskController() {
        taskDAO = new TaskDAO();
    }

    public boolean addTask(int userId, String title) {
        Task task = new Task(title, userId);
        return taskDAO.insert(task);
    }

    public boolean updateTask(Task task) {
        return taskDAO.update(task);
    }

    public boolean deleteTask(int taskId) {
        return taskDAO.delete(taskId);
    }

    public List<Task> getTasksByUser(int userId) {
        return taskDAO.findAllByUser(userId);
    }
}
