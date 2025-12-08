package com.example;

import java.sql.SQLException;

public interface AccountRepository {

    /**
     * Checks login credentials
     * @param username
     * @param password
     * @return
     * @throws SQLException
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