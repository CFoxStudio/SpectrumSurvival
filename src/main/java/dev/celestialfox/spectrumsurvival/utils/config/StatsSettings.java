package dev.celestialfox.spectrumsurvival.utils.config;

public class StatsSettings {
    public static boolean getEnabled() {
        return Configuration.getQueueAllowForce();
    }
    public static String getType() {
        return Configuration.getStatsIp();
    }
    public static String getIP() {
        return Configuration.getStatsIp();
    }
    public static int getPort() {
        return Configuration.getStatsPort();
    }
}
