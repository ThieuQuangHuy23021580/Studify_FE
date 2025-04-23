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

    public void addSchedule(Schedule schedule) {
        try {
            scheduleDAO.insert(schedule);
            System.out.println("Schedule added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding schedule: " + e.getMessage());
        }
    }

    public void updateSchedule(Schedule schedule) {
        try {
            scheduleDAO.update(schedule);
            System.out.println("Schedule updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating schedule: " + e.getMessage());
        }
    }

    public void deleteSchedule(int id) {
        try {
            scheduleDAO.delete(id);
            System.out.println("Schedule deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting schedule: " + e.getMessage());
        }
    }

    public List<Schedule> getSchedulesByUserId(int userId) {
        try {
            return scheduleDAO.getAllByUserId(userId);
        } catch (SQLException e) {
            System.err.println("Error fetching schedules: " + e.getMessage());
            return null;
        }
    }
}