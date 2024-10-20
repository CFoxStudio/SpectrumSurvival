package dev.celestialfox.spectrumsurvival.utils.stats;

import dev.celestialfox.spectrumsurvival.utils.config.Settings;
import dev.celestialfox.spectrumsurvival.utils.config.StatsSettings;
import net.minestom.server.entity.Player;

import java.util.List;

public class Stats {
    private final IStatsStorage storage;
    private final boolean isOnlineMode;

    public Stats(IStatsStorage storage, boolean isOnlineMode) {
        this.storage = storage;
        this.isOnlineMode = isOnlineMode;
    }

    public void won(List<Player> players) {
        for (Player player : players) {
            String playerId = isOnlineMode ? player.getUuid().toString() : player.getUsername();
            storage.saveWin(playerId);
        }
    }

    public void lost(List<Player> players) {
        for (Player player : players) {
            String playerId = isOnlineMode ? player.getUuid().toString() : player.getUsername();
            storage.saveLoss(playerId);
        }
    }

    public int getWins(Player player) {
        String playerId = isOnlineMode ? player.getUuid().toString() : player.getUsername();
        return storage.getWins(playerId);
    }

    public int getLosses(Player player) {
        String playerId = isOnlineMode ? player.getUuid().toString() : player.getUsername();
        return storage.getLosses(playerId);
    }

    public static Stats initialize() {
        if (!StatsSettings.getEnabled()) {
            throw new IllegalStateException("Stats saving is disabled in the config.");
        }

        String dbType = StatsSettings.getType();
        String serverMode = Settings.getMode();
        IStatsStorage storage;
        boolean isOnlineMode = resolveOnlineMode(serverMode);

        switch (dbType.toLowerCase()) {
            case "mariadb":
                storage = initializeMariaDB();
                break;

            case "sqlite":
                storage = initializeSQLite();
                break;

            case "dir":
                storage = new DirStats(StatsSettings.getDbName());
                break;

            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }

        return new Stats(storage, isOnlineMode);
    }

    private static boolean resolveOnlineMode(String serverMode) {
        return serverMode.equalsIgnoreCase("online") ||
                serverMode.equalsIgnoreCase("velocity") ||
                serverMode.equalsIgnoreCase("bungeecord");
    }

    private static IStatsStorage initializeMariaDB() {
        String ip = StatsSettings.getDbIp();
        String port = StatsSettings.getDbPort();
        String dbName = StatsSettings.getDbName();
        String user = StatsSettings.getDbUser();
        String pass = StatsSettings.getDbPassword();

        String jdbcUrl = String.format("jdbc:mariadb://%s:%s/%s", ip, port, dbName);

        try {
            return new MariaDB(jdbcUrl, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MariaDB", e);
        }
    }

    private static IStatsStorage initializeSQLite() {
        String dbPath = StatsSettings.getDbName();
        try {
            return new SQLite(dbPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLite", e);
        }
    }
}

