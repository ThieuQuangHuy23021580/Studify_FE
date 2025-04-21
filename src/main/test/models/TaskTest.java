package models;

import backend.models.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void testConstructorWithTitleAndStudentId() {
        Task task = new Task("Homework", 123);

        assertEquals("Homework", task.getTitle());
        assertFalse(task.isCompleted());
        assertEquals(123, task.getStudentId());
        assertEquals(0, task.getId());
    }

    @Test
    public void testConstructorWithTitleCompletedStudentId() {
        Task task = new Task("Project", false, 456);

        assertEquals("Project", task.getTitle());
        assertFalse(task.isCompleted());
        assertEquals(456, task.getStudentId());
    }

    @Test
    public void testConstructorWithAllFields() {
        Task task = new Task(10, "Test Task", true, 789);

        assertEquals(10, task.getId());
        assertEquals("Test Task", task.getTitle());
        assertTrue(task.isCompleted());
        assertEquals(789, task.getStudentId());
    }

    @Test
    public void testSettersAndGetters() {
        Task task = new Task();
        task.setId(5);
        task.setTitle("Update Title");
        task.setCompleted(false);
        task.setStudentId(321);

        assertEquals(5, task.getId());
        assertEquals("Update Title", task.getTitle());
        assertFalse(task.isCompleted());
        assertEquals(321, task.getStudentId());
    }

    @Test
    public void testToString() {
        Task task = new Task(1, "Reading", true, 111);
        String expected = "Task{id=1, title='Reading', completed=true}";
        assertEquals(expected, task.toString());
    }
}
