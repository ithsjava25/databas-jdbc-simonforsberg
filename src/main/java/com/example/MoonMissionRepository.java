package com.example;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MoonMissionRepository {

    /**
     * Lists the names of all spacecraft used in moon missions.
     *
     * @return a list of spacecraft names
     * @throws SQLException if a database access error occurs
     */
    List<String> listAllMoonMissions() throws SQLException;

    /**
     * Gets mission details for a specific mission ID.
     *
     * @param missionId the mission ID to look up
     * @return an {@link Optional} containing the mission if found, empty otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<MoonMission> getMissionById(int missionId) throws SQLException;

    /**
     * Counts how many missions were launched in a given year.
     *
     * @param year the launch year to count missions for
     * @return the number of missions for that year
     * @throws SQLException if a database access error occurs
     */
    int countMissionsByYear(int year) throws SQLException;
}
