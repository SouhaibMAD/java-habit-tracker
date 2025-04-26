package main.java.com.habittracker.services;

import main.java.com.habittracker.models.Habit;
import main.java.com.habittracker.models.HabitCompletion;
import main.java.com.habittracker.utils.DatabaseManager;
import main.java.com.habittracker.utils.DateUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrackingService {
    public void markHabitCompleted(int habitId, LocalDate date, boolean completed) throws SQLException {
        String sql = "INSERT OR REPLACE INTO habit_completions(habit_id, completion_date, completed) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);
            pstmt.setString(2, date.toString());
            pstmt.setBoolean(3, completed);
            pstmt.executeUpdate();
        }
    }

    public List<HabitCompletion> getHabitCompletions(int habitId) throws SQLException {
        List<HabitCompletion> completions = new ArrayList<>();
        String sql = "SELECT * FROM habit_completions WHERE habit_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    completions.add(new HabitCompletion(
                            rs.getInt("habit_id"),
                            LocalDate.parse(rs.getString("completion_date")),
                            rs.getBoolean("completed")
                    ));
                }
            }
        }
        return completions;
    }
}