package main.java.com.habittracker.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:habittracker.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        String[] sqlStatements = {
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL," +
                        "score INTEGER DEFAULT 0)",

                "CREATE TABLE IF NOT EXISTS habits (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "name TEXT NOT NULL," +
                        "description TEXT," +
                        "frequency TEXT NOT NULL," +
                        "FOREIGN KEY(user_id) REFERENCES users(id))",

                "CREATE TABLE IF NOT EXISTS habit_completions (" +
                        "habit_id INTEGER NOT NULL," +
                        "completion_date TEXT NOT NULL," +
                        "completed BOOLEAN NOT NULL," +
                        "PRIMARY KEY(habit_id, completion_date)," +
                        "FOREIGN KEY(habit_id) REFERENCES habits(id))"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
