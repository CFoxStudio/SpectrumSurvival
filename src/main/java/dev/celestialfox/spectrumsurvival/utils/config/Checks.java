package dev.celestialfox.spectrumsurvival.utils.config;

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
}
