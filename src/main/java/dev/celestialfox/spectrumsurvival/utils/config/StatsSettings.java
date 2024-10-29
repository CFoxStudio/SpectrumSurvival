package dev.celestialfox.spectrumsurvival.utils.config;

public class StatsSettings {
    public static boolean getEnabled() {
        return Configuration.getQueueAllowForce();
    }
    public static String getType() {
        return Configuration.getStatsType();
    }
    public static String getIP() {
        return Configuration.getStatsIp();
    }
    public static int getPort() {
        return Configuration.getStatsPort();
    }
    public static String getDb() {
        return Configuration.getStatsDb();
    }
    public static String getConnectionStr() {
        return Configuration.getMongoUri();
    }
    public static String getUser() {
        return Configuration.getStatsUser();
    }
    public static String getPass() {
        return Configuration.getStatsPass();
    }
}
