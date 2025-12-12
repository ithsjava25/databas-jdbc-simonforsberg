package com.example;

import java.sql.SQLException;

public interface AccountRepository {

    /**
     * Validates login credentials against the account table.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return {@code true} if credentials are valid, {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean validateLogin(String username, String password) throws SQLException;

    /**
     * Creates a new account.
     *
     * @param password  the account password
     * @param firstName the users first name
     * @param lastName  the users last name
     * @param ssn       the users social security number
     * @return the generated user_id for the new account
     * @throws SQLException if a database access error occurs
     */
    int createAccount(String password, String firstName, String lastName, String ssn) throws SQLException;

    /**
     * Updates the password of an existing account.
     *
     * @param userId      the ID of the account to update
     * @param newPassword the new password
     * @throws SQLException if a database access error occurs
     */
    void updatePassword(int userId, String newPassword) throws SQLException;

    /**
     * Deletes an account.
     *
     * @param userId the ID of the account to delete
     * @throws SQLException if a database access error occurs
     */
    void deleteAccount(int userId) throws SQLException;

}