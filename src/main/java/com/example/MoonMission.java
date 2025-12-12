package com.example;

import java.time.LocalDate;

/**
 * Represents a moon mission with its associated metadata.
 *
 * <p>This is an immutable data carrier for moon mission information
 * retrieved from the database.</p>
 *
 * @param missionId   the unique identifier for the mission
 * @param spacecraft  the name of the spacecraft
 * @param launchDate  the date the mission was launched
 * @param operator    the organization that operated the mission
 * @param missionType the type of mission (e.g., "Orbiter", "Lander")
 * @param outcome     the mission outcome (e.g., "Successful", "Failure")
 */
public record MoonMission(
        int missionId,
        String spacecraft,
        LocalDate launchDate,
        String operator,
        String missionType,
        String outcome
) {
    @Override
    public String toString() {
        return "Mission ID: " + missionId +
                "\nSpacecraft: " + spacecraft +
                "\nLaunch Date: " + launchDate +
                "\nOperator: " + operator +
                "\nMission Type: " + missionType +
                "\nOutcome: " + outcome;
    }
}