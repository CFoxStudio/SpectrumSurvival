package dev.celestialfox.spectrumsurvival.utils.config;

import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Checks {
    private static final Logger logger = LoggerFactory.getLogger(Checks.class);
    public static void worldFiles() {
        logger.info("Checking for world files...");

        File lobbyWorldFile = new File("worlds/lobby.polar");
        File gameWorldFile = new File("worlds/game.polar");

        if (lobbyWorldFile.exists()) {
            logger.debug("World 'lobby.polar' found");
        } else {
            logger.error("World 'lobby.polar' not found");
            System.exit(0);
        }

        if (gameWorldFile.exists()) {
            logger.debug("World 'game.polar' found");
        } else {
            logger.error("World 'game.polar' not found");
            System.exit(0);
        }
    }
    public static void configFile() {
        File configFile = new File(Configuration.CONFIG_FILE);

        if (!configFile.exists()) {
            Configuration.createDefaultConfig();
        } else {
            Configuration.loadConfig();
        }
    }
    public static void slots() {
        if (Settings.getSlots() <= -1) {
            logger.error("Server slots cannot be set to a number lower than 0");
            System.exit(0);
        } else if (Settings.getSlots() >= 1) {
            logger.info("Server slots are set to: %s".formatted(Settings.getSlots()));
        } else {
            logger.info("Server slots are set to unlimited (theoretically).");
        }
    }

    public static void mode() {
        String mode = Settings.getMode();
        if (mode.equals("online")) {
            logger.info("Server is running in ONLINE mode. Everyone that connects needs to have a bought copy of Minecraft.");
            MojangAuth.init();
        } else if (mode.equals("offline")) {
            logger.warn("Server is running in OFFLINE mode! " +
                    "Player verification is disabled, allowing cracked/non-premium accounts to join. " +
                    "For improved security, switch to ONLINE mode.");
        } else if (mode.equals("velocity")) {
            logger.info("Server is running in VELOCITY mode. Remember that you can connect to it only by using a Velocity Proxy.");
            VelocityProxy.enable(Settings.getProxySecret());
        } else if (mode.equals("bungeecord")) {
            logger.info("Server is running in BUNGEECORD mode. Remember that you can connect to it only by using a Bungeecord Proxy (or it's forks).");
            BungeeCordProxy.enable();
        } else {
            logger.warn("Server mode wasn't set (or was set incorrectly) in the config file! Defaulting to online mode.");
            Configuration.properties.setProperty("server.mode", "online");
        }
    }

    public static void gameSettings() {
        logger.info("Checking game settings...");
        int minSlots = GameSettings.getMinSlots();
        int maxSlots = GameSettings.getMaxSlots();
        boolean queueAllowForce = GameSettings.getAllowForce();
        int queueWaitTime = GameSettings.getWaitTime();

        if (minSlots < 2) {
            logger.error("Minimum slots cannot be less than 2.");
            System.exit(0);
        } else {
            logger.info("Minimum slots set to: " + minSlots);
        }

        if (maxSlots < minSlots) {
            logger.error("Maximum slots cannot be less than the minimum slots.");
            System.exit(0);
        } else {
            logger.info("Maximum slots set to: " + maxSlots);
        }

        if (queueAllowForce) {
            logger.info("Queue force join is enabled.");
        } else {
            logger.info("Queue force join is disabled.");
        }

        if (queueWaitTime < 0) {
            logger.error("Queue wait time cannot be negative.");
            System.exit(0);
        } else {
            logger.info("Queue wait time set to: " + queueWaitTime + " seconds.");
        }
    }
}
