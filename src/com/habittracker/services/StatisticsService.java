package main.java.com.habittracker.services;

import main.java.com.habittracker.models.Habit;
import main.java.com.habittracker.models.HabitCompletion;
import main.java.com.habittracker.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class StatisticsService {
    public double calculateSuccessRate(int habitId) throws SQLException {
        String sql = "SELECT COUNT(*) as total, SUM(CASE WHEN completed = 1 THEN 1 ELSE 0 END) as completed " +
                "FROM habit_completions WHERE habit_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int completed = rs.getInt("completed");
                    return total > 0 ? (double) completed / total * 100 : 0;
                }
            }
        }
        return 0;
    }

    public int calculateCurrentStreak(int habitId) throws SQLException {
        String sql = "SELECT completion_date FROM habit_completions " +
                "WHERE habit_id = ? AND completed = 1 " +
                "ORDER BY completion_date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return 0;

                LocalDate lastDate = LocalDate.parse(rs.getString("completion_date"));
                if (!lastDate.equals(LocalDate.now())) return 0;

                int streak = 1;
                while (rs.next()) {
                    LocalDate currentDate = LocalDate.parse(rs.getString("completion_date"));
                    if (currentDate.equals(lastDate.minusDays(1))) {
                        streak++;
                        lastDate = currentDate;
                    } else {
                        break;
                    }
                }
                return streak;
            }
        }
    }
    public int calculateLongestStreak(int habitId) throws SQLException {
        String sql = "SELECT completion_date FROM habit_completions " +
                "WHERE habit_id = ? AND completed = 1 " +
                "ORDER BY completion_date";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);
            ResultSet rs = pstmt.executeQuery();

            int maxStreak = 0;
            int currentStreak = 0;
            LocalDate previousDate = null;

            while (rs.next()) {
                LocalDate currentDate = LocalDate.parse(rs.getString("completion_date"));

                if (previousDate == null || currentDate.equals(previousDate.plusDays(1))) {
                    currentStreak++;
                } else if (!currentDate.equals(previousDate)) {
                    maxStreak = Math.max(maxStreak, currentStreak);
                    currentStreak = 1;
                }

                previousDate = currentDate;
            }

            return Math.max(maxStreak, currentStreak);
        }
    }
}