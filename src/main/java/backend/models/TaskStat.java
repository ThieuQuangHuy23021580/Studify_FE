package backend.models;

import java.time.LocalDate;

public class TaskStat {
    private LocalDate date;
    private int total;

    public TaskStat(LocalDate date, int total) {
        this.date = date;
        this.total = total;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTotal() {
        return total;
    }
}