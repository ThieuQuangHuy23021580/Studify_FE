package backend.dao;

import backend.models.Database;
import backend.models.Task;
import backend.models.TaskStat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private Connection connection;

    public TaskDAO() {
        connection = Database.getConnect();
    }

    public boolean insert(Task task) {
        String sql = "INSERT INTO tasks (title, completed, user_id, completed_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setBoolean(2, task.isCompleted());
            stmt.setInt(3, task.getStudentId());
            if (task.isCompleted()) {
                stmt.setString(4, java.time.LocalDateTime.now().toString());
            } else {
                stmt.setNull(4, java.sql.Types.NULL);
            }
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Task task) {
        String sql = "UPDATE tasks SET completed = ?, completed_at = ? " +
                "WHERE id = (SELECT MAX(id) FROM tasks WHERE user_id = ? AND title = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, task.isCompleted());
            if (task.isCompleted()) {
                stmt.setString(2, java.time.LocalDateTime.now().toString());
            } else {
                stmt.setNull(2, java.sql.Types.NULL);
            }
            stmt.setInt(3, task.getStudentId());
            stmt.setString(4, task.getTitle());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean delete(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Task> findAllByUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getBoolean("completed"),
                        rs.getInt("user_id")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<TaskStat> getCompletedTaskStatsByDay(int userId) {
        String sql = "SELECT DATE(completed_at) AS period, COUNT(*) AS total " +
                "FROM tasks WHERE completed = 1 AND user_id = ? " +
                "GROUP BY period ORDER BY period";
        return getStats(sql, userId);
    }

    public List<TaskStat> getCompletedTaskStatsByMonth(int userId) {
        String sql = "SELECT strftime('%Y-%m-01', completed_at) AS period, COUNT(*) AS total " +
                "FROM tasks WHERE completed = 1 AND user_id = ? " +
                "GROUP BY period ORDER BY period";
        return getStats(sql, userId);
    }

    public List<TaskStat> getCompletedTaskStatsByYear(int userId) {
        String sql = "SELECT strftime('%Y-01-01', completed_at) AS period, COUNT(*) AS total " +
                "FROM tasks WHERE completed = 1 AND user_id = ? " +
                "GROUP BY period ORDER BY period";
        return getStats(sql, userId);
    }

    private List<TaskStat> getStats(String sql, int userId) {
        List<TaskStat> stats = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String periodStr = rs.getString("period");
                LocalDate date = LocalDate.parse(periodStr);
                int total = rs.getInt("total");
                stats.add(new TaskStat(date, total));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}