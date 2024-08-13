package dev.celestialfox.spectrumsurvival.utils.config;

public class Settings {
    public static String getIP() {
        return Configuration.getServerIp();
    }

    public static int getPort() {
        return Configuration.getServerPort();
    }
}
