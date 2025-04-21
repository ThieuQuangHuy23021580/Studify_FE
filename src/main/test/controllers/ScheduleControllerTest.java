package controllers;

import backend.controllers.ScheduleController;
import backend.dao.ScheduleDAO;
import backend.models.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleControllerTest {

    private ScheduleDAO mockScheduleDAO;
    private ScheduleController scheduleController;

    @BeforeEach
    public void setUp() {
        mockScheduleDAO = mock(ScheduleDAO.class);
        scheduleController = new ScheduleController() {
            {
                this.scheduleDAO = mockScheduleDAO;
            }
        };
    }

    @Test
    public void testAddSchedule() throws SQLException {
        Schedule schedule = new Schedule();
        scheduleController.addSchedule(schedule);
        verify(mockScheduleDAO, times(1)).insert(schedule);
    }

    @Test
    public void testUpdateSchedule() throws SQLException {
        Schedule schedule = new Schedule();
        scheduleController.updateSchedule(schedule);
        verify(mockScheduleDAO, times(1)).update(schedule);
    }

    @Test
    public void testDeleteSchedule() throws SQLException {
        int scheduleId = 1;
        scheduleController.deleteSchedule(scheduleId);
        verify(mockScheduleDAO, times(1)).delete(scheduleId);
    }

    @Test
    public void testGetAllSchedules() throws SQLException {
        List<Schedule> mockSchedules = Arrays.asList(new Schedule(), new Schedule());
        when(mockScheduleDAO.getAll()).thenReturn(mockSchedules);

        List<Schedule> result = scheduleController.getAllSchedules();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(mockScheduleDAO, times(1)).getAll();
    }

    @Test
    public void testGetAllSchedulesOnSQLException() throws SQLException {
        when(mockScheduleDAO.getAll()).thenThrow(new SQLException("DB error"));
        List<Schedule> result = scheduleController.getAllSchedules();
        assertNull(result);
    }
}
