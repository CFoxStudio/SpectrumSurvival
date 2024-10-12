package dev.celestialfox.spectrumsurvival.utils.config;

public class Settings {
    public static String getIP() {
        return Configuration.getServerIp();
    }

    public static int getPort() {
        return Configuration.getServerPort();
    }

    public static String getMode() {
        return Configuration.getServerMode();
    }

    public static int getSlots() {
        return Configuration.getServerSlots();
    }

    public static String getProxySecret() {
        return Configuration.getProxySecret();
    }
}
