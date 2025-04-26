package main.java.com.habittracker.models;

import java.time.LocalDate;

public class HabitCompletion {
    private int habitId;
    private LocalDate date;
    private boolean completed;

    public HabitCompletion(int habitId, LocalDate date, boolean completed) {
        this.habitId = habitId;
        this.date = date;
        this.completed = completed;
    }

    // Getters
    public int getHabitId() { return habitId; }
    public LocalDate getDate() { return date; }
    public boolean isCompleted() { return completed; }
}