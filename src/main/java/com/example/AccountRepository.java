package com.example;

import java.sql.SQLException;

public interface AccountRepository {

    /**
     * Checks login credentials
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return true if credentials are valid, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean validateLogin(String username, String password) throws SQLException;

    /**
     * Creates a new account
     *
     * @param password
     * @param firstName
     * @param lastName
     * @param ssn
     * @return
     * @throws SQLException
     */
    int createAccount(String password, String firstName, String lastName, String ssn) throws SQLException;

    /**
     * Updates password for an existing account
     *
     * @param userId
     * @param newPassword
     * @throws SQLException
     */
    void updatePassword(int userId, String newPassword) throws SQLException;

    /**
     * Delete an account
     *
     * @param userId
     * @throws SQLException
     */
    void deleteAccount(int userId) throws SQLException;

}