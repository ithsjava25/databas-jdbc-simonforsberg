package com.example;

import javax.sql.DataSource;
import java.sql.*;

/**
 * JDBC-based implementation of {@link AccountRepository}.
 *
 * <p>This implementation uses a {@link DataSource} to obtain database connections
 * and executes SQL statements against the {@code account} table.</p>
 *
 * <p>All methods use {@link PreparedStatement} to prevent SQL injection attacks.</p>
 */
public class JdbcAccountRepository implements AccountRepository {

    private final DataSource dataSource;

    public JdbcAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT user_id FROM account WHERE name = ? AND password = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int createAccount(String password, String firstName, String lastName, String ssn) throws SQLException {
        String sql = "INSERT INTO account(password, first_name, last_name, ssn) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, password);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, ssn);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Failed to get generated key.");
            }
        }
    }

    @Override
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE account SET password = ? WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No account found with user ID: " + userId);
            }
        }
    }

    @Override
    public void deleteAccount(int userId) throws SQLException {
        String sql = "DELETE FROM account WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No account found with user ID: " + userId);
            }
        }
    }
}
