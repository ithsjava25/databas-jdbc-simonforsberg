package com.example;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main application class for the Moon Mission CLI.
 *
 * <p>This class provides a console-based interface for managing moon missions and user accounts.
 * It supports development mode using Testcontainers for easy local testing.</p>
 *
 * @see AccountRepository
 * @see MoonMissionRepository
 */
public class Main {

    /**
     * Entry point and main application controller.
     *
     * <p>Handles development mode initialization, loads database configuration from
     * system properties or environment variables, constructs repositories, and
     * provides a simple console-based menu for interacting with the application.</p>
     *
     * <p>In development mode, a temporary MySQL database is started using Testcontainers.</p>
     */
    static void main(String[] args) {
        if (isDevMode(args)) {
            DevDatabaseInitializer.start();
        }
        new Main().run();
    }

    /**
     * Initializes the application, resolves database configuration,
     * creates repository instances, and starts the interactive console loop.
     *
     * @throws IllegalStateException if required DB configuration is missing
     */
    public void run() {
        // Resolve DB settings with precedence: System properties -> Environment variables
        String jdbcUrl = resolveConfig("APP_JDBC_URL", "APP_JDBC_URL");
        String dbUser = resolveConfig("APP_DB_USER", "APP_DB_USER");
        String dbPass = resolveConfig("APP_DB_PASS", "APP_DB_PASS");

        if (jdbcUrl == null || dbUser == null || dbPass == null) {
            throw new IllegalStateException(
                    "Missing DB configuration. Provide APP_JDBC_URL, APP_DB_USER, APP_DB_PASS " +
                            "as system properties (-Dkey=value) or environment variables.");
        }

        // Create DatasSource and Repositories
        DataSource dataSource = new SimpleDriverManagerDataSource(jdbcUrl, dbUser, dbPass);
        AccountRepository accountRepo = new JdbcAccountRepository(dataSource);
        MoonMissionRepository missionRepo = new JdbcMoonMissionRepository(dataSource);

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {

                Boolean result = login(scanner, accountRepo);

                if (result == null) {
                    System.out.println("Exiting...");
                    return;
                }

                if (result) {
                    handleMenu(scanner, accountRepo, missionRepo);
                    return;
                }

                System.out.println("Try again or enter 0 to exit.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean login(Scanner scanner, AccountRepository accountRepo) throws SQLException {
        System.out.print("Username (or enter 0 to exit): ");
        String username = scanner.nextLine();

        if (username.equals("0")) {
            return null;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (accountRepo.validateLogin(username, password)) {
            return true;
        } else {
            System.out.println("Invalid username or password");
            return false;
        }
    }

    private void handleMenu(Scanner scanner, AccountRepository accountRepo,
                            MoonMissionRepository missionRepo) throws SQLException {
        while (true) {
            System.out.println("\n----=== MENU ===----");
            System.out.println("1) List moon missions");
            System.out.println("2) Get a moon mission by mission ID");
            System.out.println("3) Count missions for a given year");
            System.out.println("4) Create an account");
            System.out.println("5) Update an account password");
            System.out.println("6) Delete an account");
            System.out.println("0) Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> listMoonMissions(missionRepo);
                case "2" -> getMissionById(scanner, missionRepo);
                case "3" -> countMissionsByYear(scanner, missionRepo);
                case "4" -> createAccount(scanner, accountRepo);
                case "5" -> updatePassword(scanner, accountRepo);
                case "6" -> deleteAccount(scanner, accountRepo);
                case "0" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void listMoonMissions(MoonMissionRepository repo) throws SQLException {
        List<String> spacecrafts = repo.listAllMoonMissions();
        System.out.println("\nMoon Missions:");
        for (String spacecraft : spacecrafts) {
            System.out.println("- " + spacecraft);
        }
    }

    private void getMissionById(Scanner scanner, MoonMissionRepository repo) throws SQLException {
        System.out.print("Enter mission ID: ");
        int missionId;
        try {
            missionId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
            return;
        }
        Optional<MoonMission> mission = repo.getMissionById(missionId);
        if (mission.isPresent()) {
            System.out.println("\n" + mission.get());
        } else {
            System.out.println("Mission not found");
        }
    }

    private void countMissionsByYear(Scanner scanner, MoonMissionRepository repo) throws SQLException {
        System.out.print("Enter year: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        int count = repo.countMissionsByYear(year);
        System.out.println("Number of missions in " + year + ": " + count);
    }

    private void createAccount(Scanner scanner, AccountRepository repo) throws SQLException {
        System.out.print("First name: ");
        String firstName = scanner.nextLine().trim();
        if (firstName.isEmpty()) {
            System.out.println("First name cannot be empty. Please try again.");
            return;
        }

        System.out.print("Last name: ");
        String lastName = scanner.nextLine().trim();
        if (lastName.isEmpty()) {
            System.out.println("Last name cannot be empty. Please try again.");
            return;
        }

        System.out.print("SSN (YYMMDD-XXXX): ");
        String ssn = scanner.nextLine().trim();
        if (!ssn.matches("\\d{6}-\\d{4}")) {
            System.out.println("Invalid SSN format. Use YYMMDD-XXXX. Please try again.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters. Please try again.");
            return;
        }

        try {
            int userId = repo.createAccount(password, firstName, lastName, ssn);
            System.out.println("Account created with user ID: " + userId);
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private void updatePassword(Scanner scanner, AccountRepository repo) throws SQLException {
        System.out.print("User ID: ");
        int userId;
        try {
            userId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        System.out.print("New password: ");
        String newPassword = scanner.nextLine();

        repo.updatePassword(userId, newPassword);
        System.out.println("Password updated successfully");
    }

    private void deleteAccount(Scanner scanner, AccountRepository repo) throws SQLException {
        System.out.print("User ID: ");
        int userId;
        try {
            userId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
            return;
        }

        repo.deleteAccount(userId);
        System.out.println("Account deleted successfully");
    }

    /**
     * Determines if the application is running in development mode based on system properties,
     * environment variables, or command-line arguments.
     *
     * @param args an array of command-line arguments
     * @return {@code true} if the application is in development mode; {@code false} otherwise
     */
    private static boolean isDevMode(String[] args) {
        if (Boolean.getBoolean("devMode"))  //Add VM option -DdevMode=true
            return true;
        if ("true".equalsIgnoreCase(System.getenv("DEV_MODE")))  //Environment variable DEV_MODE=true
            return true;
        return Arrays.asList(args).contains("--dev"); //Argument --dev
    }

    /**
     * Reads configuration with precedence: Java system property first, then environment variable.
     * Returns trimmed value or null if neither source provides a non-empty value.
     */
    private static String resolveConfig(String propertyKey, String envKey) {
        String v = System.getProperty(propertyKey);
        if (v == null || v.trim().isEmpty()) {
            v = System.getenv(envKey);
        }
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }
}
