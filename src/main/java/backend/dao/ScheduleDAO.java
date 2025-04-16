package backend.dao;

import backend.models.Schedule;
import backend.models.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    private final Connection connection;

    public ScheduleDAO() {
        this.connection = Database.getConnect();
    }

    public void insert(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO schedules (course, period, day_of_week, room) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, schedule.getCourse());
        stmt.setString(2, schedule.getPeriod());
        stmt.setString(3, schedule.getDayOfWeek());
        stmt.setString(4, schedule.getRoom());
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int scheduleId = rs.getInt(1);
            insertStudentIds(scheduleId, schedule.getStudentIds());
        }
    }

    private void insertStudentIds(int scheduleId, List<Integer> studentIds) throws SQLException {
        String sql = "INSERT INTO schedule_students (schedule_id, student_id) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int studentId : studentIds) {
            stmt.setInt(1, scheduleId);
            stmt.setInt(2, studentId);
            stmt.addBatch();
        }
        stmt.executeBatch();
    }

    public List<Schedule> getAll() throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules";
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int scheduleId = rs.getInt("id");
            List<Integer> studentIds = getStudentIds(scheduleId);

            Schedule schedule = new Schedule(
                    scheduleId,
                    rs.getString("course"),
                    rs.getString("period"),
                    rs.getString("day_of_week"),
                    rs.getString("room"),
                    studentIds
            );
            schedules.add(schedule);
        }
        return schedules;
    }

    private List<Integer> getStudentIds(int scheduleId) throws SQLException {
        List<Integer> studentIds = new ArrayList<>();
        String sql = "SELECT student_id FROM schedule_students WHERE schedule_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, scheduleId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            studentIds.add(rs.getInt("student_id"));
        }
        return studentIds;
    }

    public void delete(int scheduleId) throws SQLException {
        String deleteLinks = "DELETE FROM schedule_students WHERE schedule_id = ?";
        String deleteSchedule = "DELETE FROM schedules WHERE id = ?";

        PreparedStatement stmt1 = connection.prepareStatement(deleteLinks);
        stmt1.setInt(1, scheduleId);
        stmt1.executeUpdate();

        PreparedStatement stmt2 = connection.prepareStatement(deleteSchedule);
        stmt2.setInt(1, scheduleId);
        stmt2.executeUpdate();
    }

    public void update(Schedule schedule) throws SQLException {
        String sql = "UPDATE schedules SET course = ?, period = ?, day_of_week = ?, room = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, schedule.getCourse());
        stmt.setString(2, schedule.getPeriod());
        stmt.setString(3, schedule.getDayOfWeek());
        stmt.setString(4, schedule.getRoom());
        stmt.setInt(5, schedule.getId());
        stmt.executeUpdate();

        // Xóa và cập nhật studentIds
        String deleteOld = "DELETE FROM schedule_students WHERE schedule_id = ?";
        PreparedStatement deleteStmt = connection.prepareStatement(deleteOld);
        deleteStmt.setInt(1, schedule.getId());
        deleteStmt.executeUpdate();

        insertStudentIds(schedule.getId(), schedule.getStudentIds());
    }
}
