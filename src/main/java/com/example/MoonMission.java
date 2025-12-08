package com.example;

import java.time.LocalDate;

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