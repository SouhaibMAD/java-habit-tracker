package main.java.com.habittracker;

import main.java.com.habittracker.models.Habit;
import main.java.com.habittracker.models.User;
import main.java.com.habittracker.models.HabitCompletion;
import main.java.com.habittracker.services.*;
import main.java.com.habittracker.utils.ConsoleUtils;
import main.java.com.habittracker.utils.DatabaseManager;
import main.java.com.habittracker.exceptions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private static final UserService userService = new UserService();
    private static final HabitService habitService = new HabitService();
    private static final TrackingService trackingService = new TrackingService();
    private static final StatisticsService statisticsService = new StatisticsService();

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        boolean running = true;
        while (running) {
            try {
                if (!userService.isUserLoggedIn()) {
                    showAuthMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                ConsoleUtils.pressEnterToContinue();
            }
        }
    }

    private static void showAuthMenu() throws SQLException {
        ConsoleUtils.clearScreen();
        String[] options = {"Register", "Login", "Exit"};
        ConsoleUtils.displayMenu("Habit Tracker - Authentication", options);

        int choice = ConsoleUtils.getIntInput("");
        switch (choice) {
            case 1 -> registerUser();
            case 2 -> loginUser();
            case 3 -> System.exit(0);
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    private static void showMainMenu() throws SQLException, HabitNotFoundException {
        ConsoleUtils.clearScreen();
        String[] options = {
                "Add new habit",
                "List my habits",
                "Mark habit as completed",
                "View habit history",
                "View statistics",
                "Logout"
        };
        ConsoleUtils.displayMenu("Habit Tracker - Main Menu", options);

        int choice = ConsoleUtils.getIntInput("");
        switch (choice) {
            case 1 -> addNewHabit();
            case 2 -> listUserHabits();
            case 3 -> markHabitCompleted();
            case 4 -> viewHabitHistory();
            case 5 -> viewStatistics();
            case 6 -> userService.logout();
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    private static void registerUser() throws SQLException {
        String username = ConsoleUtils.getStringInput("Enter username: ");
        String password = ConsoleUtils.getStringInput("Enter password: ");
        userService.registerUser(username, password);
        System.out.println("Registration successful!");
        ConsoleUtils.pressEnterToContinue();
    }

    private static void loginUser() {
        String username = ConsoleUtils.getStringInput("Enter username: ");
        String password = ConsoleUtils.getStringInput("Enter password: ");

        try {
            userService.login(username, password);
            System.out.println("Login successful!");
        } catch (UserNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void addNewHabit() throws SQLException {
        String name = ConsoleUtils.getStringInput("Enter habit name: ");
        String description = ConsoleUtils.getStringInput("Enter habit description: ");

        System.out.println("Select frequency:");
        System.out.println("1. Daily");
        System.out.println("2. Weekly");
        System.out.println("3. Monthly");
        int freqChoice = ConsoleUtils.getIntInput("Your choice: ");

        Habit.Frequency frequency = switch (freqChoice) {
            case 1 -> Habit.Frequency.DAILY;
            case 2 -> Habit.Frequency.WEEKLY;
            case 3 -> Habit.Frequency.MONTHLY;
            default -> {
                System.out.println("Invalid choice, setting to Daily");
                yield Habit.Frequency.DAILY;
            }
        };

        habitService.addHabit(userService.getCurrentUser(), name, description, frequency);
        System.out.println("Habit added successfully!");
        ConsoleUtils.pressEnterToContinue();
    }

    private static void listUserHabits() throws SQLException {
        List<Habit> habits = habitService.getUserHabits(userService.getCurrentUser());

        if (habits.isEmpty()) {
            System.out.println("No habits found.");
        } else {
            System.out.println("\nYour habits:");
            for (int i = 0; i < habits.size(); i++) {
                Habit habit = habits.get(i);
                System.out.printf("%d. %s - %s (Frequency: %s)%n",
                        i + 1, habit.getName(), habit.getDescription(), habit.getFrequency());
            }
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void markHabitCompleted() throws SQLException, HabitNotFoundException {
        List<Habit> habits = habitService.getUserHabits(userService.getCurrentUser());

        if (habits.isEmpty()) {
            System.out.println("No habits to mark as completed.");
            return;
        }

        System.out.println("\nSelect habit to mark:");
        for (int i = 0; i < habits.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, habits.get(i).getName());
        }

        int habitChoice = ConsoleUtils.getIntInput("Your choice: ") - 1;
        if (habitChoice < 0 || habitChoice >= habits.size()) {
            System.out.println("Invalid habit choice.");
            return;
        }

        Habit selectedHabit = habits.get(habitChoice);
        String dateInput = ConsoleUtils.getStringInput("Enter date (YYYY-MM-DD) or leave blank for today: ");
        LocalDate date = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);

        trackingService.markHabitCompleted(selectedHabit.getId(), date, true);
        System.out.printf("Habit '%s' marked as completed for %s%n",
                selectedHabit.getName(), date);
        ConsoleUtils.pressEnterToContinue();
    }

    private static void viewHabitHistory() throws SQLException, HabitNotFoundException {
        List<Habit> habits = habitService.getUserHabits(userService.getCurrentUser());

        if (habits.isEmpty()) {
            System.out.println("No habits available to show history.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        System.out.println("\nSelect habit to view history:");
        for (int i = 0; i < habits.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, habits.get(i).getName());
        }

        int habitChoice = ConsoleUtils.getIntInput("Your choice: ") - 1;
        if (habitChoice < 0 || habitChoice >= habits.size()) {
            System.out.println("Invalid habit choice.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        Habit selectedHabit = habits.get(habitChoice);
        List<HabitCompletion> completions = trackingService.getHabitCompletions(selectedHabit.getId());

        if (completions.isEmpty()) {
            System.out.printf("\nNo completion history found for '%s'%n", selectedHabit.getName());
        } else {
            System.out.printf("\nCompletion history for '%s':%n", selectedHabit.getName());
            System.out.println("Date       | Status");
            System.out.println("-------------------");
            completions.forEach(c -> {
                String status = c.isCompleted() ? "✓ Completed" : "✗ Missed";
                System.out.printf("%s | %s%n", c.getDate(), status);
            });

            // Calculate and display some basic stats
            long total = completions.size();
            long completedCount = completions.stream().filter(HabitCompletion::isCompleted).count();
            double completionRate = total > 0 ? (double) completedCount / total * 100 : 0;

            System.out.println("\nSummary:");
            System.out.printf("- Total entries: %d%n", total);
            System.out.printf("- Completion rate: %.1f%%%n", completionRate);
        }
        ConsoleUtils.pressEnterToContinue();
    }

    private static void viewStatistics() throws SQLException, HabitNotFoundException {
        List<Habit> habits = habitService.getUserHabits(userService.getCurrentUser());

        if (habits.isEmpty()) {
            System.out.println("No habits available to show statistics.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }

        System.out.println("\n=== Habit Statistics ===");

        for (Habit habit : habits) {
            double successRate = statisticsService.calculateSuccessRate(habit.getId());
            int currentStreak = statisticsService.calculateCurrentStreak(habit.getId());

            System.out.printf("\nHabit: %s%n", habit.getName());
            System.out.printf("- Success rate: %.1f%%%n", successRate);
            System.out.printf("- Current streak: %d days%n", currentStreak);

            // Additional motivational message based on performance
            if (successRate > 75) {
                System.out.println("- Great job! You're doing amazing!");
            } else if (successRate > 50) {
                System.out.println("- Good progress! Keep it up!");
            } else {
                System.out.println("- You can do better! Stay consistent!");
            }
        }

        // Calculate and display overall user statistics
        int totalHabits = habits.size();
        int totalCompletions = habits.stream()
                .mapToInt(h -> {
                    try {
                        return trackingService.getHabitCompletions(h.getId()).size();
                    } catch (SQLException e) {
                        return 0;
                    }
                })
                .sum();

        System.out.println("\n=== Overall Statistics ===");
        System.out.printf("- Total habits: %d%n", totalHabits);
        System.out.printf("- Total completions: %d%n", totalCompletions);

        ConsoleUtils.pressEnterToContinue();
    }
}