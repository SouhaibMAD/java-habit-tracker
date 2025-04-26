package main.java.com.habittracker.services;


import main.java.com.habittracker.models.User;
import main.java.com.habittracker.exceptions.UserNotFoundException;
import main.java.com.habittracker.utils.DatabaseManager;

import java.sql.*;

public class UserService {
    private User currentUser;

    public void registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    this.currentUser = new User(id, username, password);
                }
            }
        }
    }

    public boolean login(String username, String password) throws UserNotFoundException, SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.currentUser = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                    this.currentUser.setScore(rs.getInt("score"));
                    return true;
                }
            }
        }
        throw new UserNotFoundException("User not found or password incorrect");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }
}