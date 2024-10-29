package dev.celestialfox.spectrumsurvival.utils.stats;

import dev.celestialfox.spectrumsurvival.utils.config.Settings;
import dev.celestialfox.spectrumsurvival.utils.config.StatsSettings;
import dev.celestialfox.spectrumsurvival.utils.stats.impl.DirStats;
import dev.celestialfox.spectrumsurvival.utils.stats.impl.MariaDB;
import dev.celestialfox.spectrumsurvival.utils.stats.impl.MongoDB;
import dev.celestialfox.spectrumsurvival.utils.stats.impl.SQLite;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Stats {
    private static final Logger logger = LoggerFactory.getLogger(Stats.class);
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
        logger.info("Initializing Stats...");
        String dbType = StatsSettings.getType();
        String serverMode = Settings.getMode();
        IStatsStorage storage;
        boolean isOnlineMode = resolveOnlineMode(serverMode);
        logger.info("Using database type: {}", dbType);

        switch (dbType.toLowerCase()) {
            case "mariadb":
                storage = initializeMariaDB();
                break;
            case "sqlite":
                storage = initializeSQLite();
                break;
            case "mongodb":
                storage = initializeMongoDB();
                break;
            case "dir":
                storage = new DirStats(StatsSettings.getDb());
                break;
            default:
                logger.error("Unsupported database type: {}", dbType);
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
        logger.info("Stats initialized successfully with {} database.", dbType);
        return new Stats(storage, isOnlineMode);
    }

    private static boolean resolveOnlineMode(String serverMode) {
        return serverMode.equalsIgnoreCase("online") ||
                serverMode.equalsIgnoreCase("velocity") ||
                serverMode.equalsIgnoreCase("bungeecord");
    }

    private static IStatsStorage initializeMariaDB() {
        String ip = StatsSettings.getIP();
        int port = StatsSettings.getPort();
        String dbName = StatsSettings.getDb();
        String user = StatsSettings.getUser();
        String pass = StatsSettings.getPass();
        String jdbcUrl = String.format("jdbc:mariadb://%s:%s/%s", ip, port, dbName);

        logger.info("Connecting to MariaDB at {}:{}", ip, port);
        try {
            return new MariaDB(jdbcUrl, user, pass);
        } catch (Exception e) {
            logger.error("Failed to initialize MariaDB", e);
            throw new RuntimeException("Failed to initialize MariaDB", e);
        }
    }

    private static IStatsStorage initializeSQLite() {
        String dbPath = StatsSettings.getDb();

        logger.info("Initializing SQLite with database path: {}", dbPath);
        try {
            return new SQLite(dbPath);
        } catch (Exception e) {
            logger.error("Failed to initialize SQLite", e);
            throw new RuntimeException("Failed to initialize SQLite", e);
        }
    }

    private static IStatsStorage initializeMongoDB() {
        String connectionString = StatsSettings.getConnectionStr();
        String databaseName = StatsSettings.getDb();

        logger.info("Connecting to MongoDB at {}", connectionString);
        try {
            return new MongoDB(connectionString, databaseName);
        } catch (Exception e) {
            logger.error("Failed to initialize MongoDB", e);
            throw new RuntimeException("Failed to initialize MongoDB", e);
        }
    }
}
