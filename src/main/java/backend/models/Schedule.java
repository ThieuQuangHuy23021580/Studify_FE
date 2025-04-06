package backend.models;

import java.util.List;

public class Schedule {
    private int id;
    private String course;
    private String period;
    private String dayOfWeek;
    private List<Integer> studentIds;

    public Schedule() {}

    public Schedule(int id, String course, String period, String dayOfWeek, List<Integer> studentIds) {
        this.id = id;
        this.course = course;
        this.period = period;
        this.dayOfWeek = dayOfWeek;
        this.studentIds = studentIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<Integer> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Integer> studentIds) {
        this.studentIds = studentIds;
    }
}
