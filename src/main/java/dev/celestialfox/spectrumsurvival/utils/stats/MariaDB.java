package dev.celestialfox.spectrumsurvival.utils.stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MariaDB implements IStatsStorage {
    private Connection connection;

    public MariaDB(String url, String username, String password) throws Exception {
        connection = DriverManager.getConnection(url, username, password);
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS player_stats (player_id VARCHAR(36), wins INT, losses INT, PRIMARY KEY (player_id))"
        )) {
            stmt.execute();
        }
    }

    @Override
    public void saveWin(String playerId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO player_stats (player_id, wins, losses) VALUES (?, 1, 0) ON DUPLICATE KEY UPDATE wins = wins + 1"
        )) {
            stmt.setString(1, playerId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveLoss(String playerId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO player_stats (player_id, wins, losses) VALUES (?, 0, 1) ON DUPLICATE KEY UPDATE losses = losses + 1"
        )) {
            stmt.setString(1, playerId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getWins(String playerId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT wins FROM player_stats WHERE player_id = ?"
        )) {
            stmt.setString(1, playerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("wins");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getLosses(String playerId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT losses FROM player_stats WHERE player_id = ?"
        )) {
            stmt.setString(1, playerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("losses");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}