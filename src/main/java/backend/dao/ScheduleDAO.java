package backend.dao;

import backend.models.Database;
import backend.models.Schedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    public void insert(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO schedules (course_name, day, period, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, schedule.getCourseName());
            stmt.setString(2, schedule.getDay());
            stmt.setInt(3, schedule.getPeriod());
            stmt.setInt(4, schedule.getUserId());
            stmt.executeUpdate();
        }
    }

    public void update(Schedule schedule) throws SQLException {
        String sql = "UPDATE schedules SET course_name = ?, day = ?, period = ?, user_id = ? WHERE id = ?";
        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, schedule.getCourseName());
            stmt.setString(2, schedule.getDay());
            stmt.setInt(3, schedule.getPeriod());
            stmt.setInt(4, schedule.getUserId());
            stmt.setInt(5, schedule.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int scheduleId) throws SQLException {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, scheduleId);
            stmt.executeUpdate();
        }
    }
    public void delete(String day, int period) throws SQLException {
        String sql = "DELETE FROM schedules WHERE day = ? AND period = ?";
        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, day);
            stmt.setInt(2, period);
            stmt.executeUpdate();
        }
    }

    public List<Schedule> getAllByUserId(int userId) throws SQLException {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM schedules WHERE user_id = ?";
        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getString("course_name"),
                        rs.getString("day"),
                        rs.getInt("period"),
                        rs.getInt("user_id")
                );
                schedule.setId(rs.getInt("id"));
                list.add(schedule);
            }
        }
        return list;
    }
}