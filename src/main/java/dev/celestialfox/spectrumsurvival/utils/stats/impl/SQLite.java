package dev.celestialfox.spectrumsurvival.utils.stats.impl;

import dev.celestialfox.spectrumsurvival.utils.stats.IStatsStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLite implements IStatsStorage {
    private Connection connection;

    public SQLite(String dbPath) throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS player_stats (player_id TEXT PRIMARY KEY, wins INT, losses INT)"
        )) {
            stmt.execute();
        }
    }

    @Override
    public void saveWin(String playerId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO player_stats (player_id, wins, losses) VALUES (?, 1, 0) ON CONFLICT(player_id) DO UPDATE SET wins = wins + 1"
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
                "INSERT INTO player_stats (player_id, wins, losses) VALUES (?, 0, 1) ON CONFLICT(player_id) DO UPDATE SET losses = losses + 1"
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
