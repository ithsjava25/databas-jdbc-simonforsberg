package com.example;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMoonMissionRepository implements MoonMissionRepository {
    private final DataSource dataSource;

    public JdbcMoonMissionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> listAllMoonMissions() throws SQLException {
        String sql = "SELECT spacecraft FROM moon_mission";
        List<String> spacecrafts = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String spacecraft = rs.getString("spacecraft");
                if (spacecraft != null) {
                    spacecrafts.add(spacecraft);
                }
            }
        }
        return spacecrafts;
    }

    @Override
    public Optional<MoonMission> getMissionById(int missionId) throws SQLException {
        String sql = "SELECT mission_id, spacecraft, launch_date, operator, mission_type, outcome FROM moon_mission WHERE mission_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, missionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date launchDateSql = rs.getDate("launch_date");
                    MoonMission mission = new MoonMission(
                            rs.getInt("mission_id"),
                            defaultIfNull(rs.getString("spacecraft")),
                            launchDateSql != null ? launchDateSql.toLocalDate() : null,
                            defaultIfNull(rs.getString("operator")),
                            defaultIfNull(rs.getString("mission_type")),
                            defaultIfNull(rs.getString("outcome"))
                    );
                    return Optional.of(mission);
                }
                return Optional.empty();
            }
        }
    }

    private static String defaultIfNull(String value) {
        return value != null ? value : "";
    }

    @Override
    public int countMissionsByYear(int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM moon_mission WHERE EXTRACT(YEAR FROM launch_date) = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}
