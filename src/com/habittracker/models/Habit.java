package main.java.com.habittracker.models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Habit {
    private int id;
    private int userId;
    private String name;
    private String description;
    private Frequency frequency;
    private Map<LocalDate, Boolean> completions;

    public enum Frequency {
        DAILY, WEEKLY, MONTHLY
    }

    public Habit(int id, int userId, String name, String description, Frequency frequency) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.completions = new HashMap<>();
    }

    // Getters et setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Frequency getFrequency() { return frequency; }
    public Map<LocalDate, Boolean> getCompletions() { return completions; }

    public void markCompleted(LocalDate date, boolean completed) {
        completions.put(date, completed);
    }
}