package dev.celestialfox.spectrumsurvival.utils.config;

public class GameSettings {
    public static int getMinSlots() {
        return Configuration.getGameMinSlots();
    }
    public static int getMaxSlots() {
        return Configuration.getGameMaxSlots();
    }
    public static int getWaitTime() {
        return Configuration.getQueueWaitTime();
    }
    public static boolean getAllowForce() {
        return Configuration.getQueueAllowForce();
    }
}
