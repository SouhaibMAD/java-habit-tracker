package main.java.com.habittracker.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String password;
    private List<Habit> habits;
    private int score;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.habits = new ArrayList<>();
        this.score = 0;
    }

    // Getters et setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<Habit> getHabits() { return habits; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}