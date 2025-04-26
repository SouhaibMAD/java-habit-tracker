package main.java.com.habittracker.services;

import main.java.com.habittracker.models.Habit;
import main.java.com.habittracker.models.User;
import main.java.com.habittracker.exceptions.HabitNotFoundException;
import main.java.com.habittracker.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitService {
    public void addHabit(User user, String name, String description, Habit.Frequency frequency) throws SQLException {
        String sql = "INSERT INTO habits(user_id, name, description, frequency) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getId());
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setString(4, frequency.toString());
            pstmt.executeUpdate();
        }
    }

    public List<Habit> getUserHabits(User user) throws SQLException {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habits WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Habit habit = new Habit(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            Habit.Frequency.valueOf(rs.getString("frequency"))
                    );
                    habits.add(habit);
                }
            }
        }
        return habits;
    }

    public Habit getHabitById(int habitId) throws SQLException, HabitNotFoundException {
        String sql = "SELECT * FROM habits WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Habit(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            Habit.Frequency.valueOf(rs.getString("frequency"))
                    );
                }
            }
        }
        throw new HabitNotFoundException("Habit not found with ID: " + habitId);
    }
}
