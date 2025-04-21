package models;

import backend.models.TaskStat;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStatTest {

    @Test
    public void testConstructorAndGetters() {
        LocalDate date = LocalDate.of(2024, 4, 21);
        int total = 5;

        TaskStat stat = new TaskStat(date, total);

        assertEquals(date, stat.getDate());
        assertEquals(5, stat.getTotal());
    }
}
