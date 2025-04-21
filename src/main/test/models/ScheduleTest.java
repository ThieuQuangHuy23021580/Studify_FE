package models;

import backend.models.Schedule;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTest {

    @Test
    public void testConstructorWithAllFields() {
        List<Integer> studentIds = Arrays.asList(101, 102, 103);
        Schedule schedule = new Schedule(1, "Math", "Morning", "Monday", "Room A", studentIds);

        assertEquals(1, schedule.getId());
        assertEquals("Math", schedule.getCourse());
        assertEquals("Morning", schedule.getPeriod());
        assertEquals("Monday", schedule.getDayOfWeek());
        assertEquals("Room A", schedule.getRoom());
        assertEquals(studentIds, schedule.getStudentIds());
    }

    @Test
    public void testSettersAndGetters() {
        Schedule schedule = new Schedule();
        List<Integer> ids = Arrays.asList(201, 202);

        schedule.setId(5);
        schedule.setCourse("Physics");
        schedule.setPeriod("Afternoon");
        schedule.setDayOfWeek("Wednesday");
        schedule.setRoom("Lab 3");
        schedule.setStudentIds(ids);

        assertEquals(5, schedule.getId());
        assertEquals("Physics", schedule.getCourse());
        assertEquals("Afternoon", schedule.getPeriod());
        assertEquals("Wednesday", schedule.getDayOfWeek());
        assertEquals("Lab 3", schedule.getRoom());
        assertEquals(ids, schedule.getStudentIds());
    }

    @Test
    public void testEmptyConstructorDefaults() {
        Schedule schedule = new Schedule();

        assertEquals(0, schedule.getId());
        assertNull(schedule.getCourse());
        assertNull(schedule.getPeriod());
        assertNull(schedule.getDayOfWeek());
        assertNull(schedule.getRoom());
        assertNull(schedule.getStudentIds());
    }
}
