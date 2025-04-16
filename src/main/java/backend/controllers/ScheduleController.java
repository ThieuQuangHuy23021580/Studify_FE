package backend.controllers;

import backend.dao.ScheduleDAO;
import backend.models.Schedule;

import java.sql.SQLException;
import java.util.List;

public class ScheduleController {
    private final ScheduleDAO scheduleDAO;

    public ScheduleController() {
        this.scheduleDAO = new ScheduleDAO();
    }

    /** Thêm một lịch học mới */
    public void addSchedule(Schedule schedule) {
        try {
            scheduleDAO.insert(schedule);
            System.out.println("Schedule added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding schedule: " + e.getMessage());
        }
    }

    /** Cập nhật lịch học */
    public void updateSchedule(Schedule schedule) {
        try {
            scheduleDAO.update(schedule);
            System.out.println("Schedule updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating schedule: " + e.getMessage());
        }
    }

    /** Xóa lịch học theo ID */
    public void deleteSchedule(int scheduleId) {
        try {
            scheduleDAO.delete(scheduleId);
            System.out.println("Schedule deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting schedule: " + e.getMessage());
        }
    }

    /** Lấy tất cả lịch học */
    public List<Schedule> getAllSchedules() {
        try {
            return scheduleDAO.getAll();
        } catch (SQLException e) {
            System.err.println("Error retrieving schedules: " + e.getMessage());
            return null;
        }
    }
}
