package com.example;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MoonMissionRepository {

    /**
     * Lists all spacecraft names.
     */
    List<String> listAllMoonMissions() throws SQLException;

    /**
     * Gets mission details by ID.
     *
     * @return Optional containing mission if found, empty otherwise
     */
    Optional<MoonMission> getMissionById(int missionId) throws SQLException;

    /**
     * Counts missions launched in a given year.
     */
    int countMissionsByYear(int year) throws SQLException;
}
