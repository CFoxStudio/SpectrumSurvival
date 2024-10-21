package dev.celestialfox.spectrumsurvival.utils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    public static final String CONFIG_FILE = "config.properties";
    public static Properties properties = new Properties();

    public static void loadConfig() {
        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
            logger.info("Config file loaded successfully.");
        } catch (IOException e) {
            logger.error("Error while loading config file: " + e.getMessage());
        }
    }

    public static void createDefaultConfig() {
        logger.info("Config file does not exist. Creating default config file...");
        try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
            properties.setProperty("server.ip", "0.0.0.0");
            properties.setProperty("server.port", "25565");
            properties.setProperty("server.mode", "online");
            properties.setProperty("server.slots", "0");
            properties.setProperty("server.proxysecret", "");
            properties.setProperty("game.minslots", "4");
            properties.setProperty("game.maxslots", "12");
            properties.setProperty("game.waittime", "5");
            properties.setProperty("game.allowforce", "true");
            properties.setProperty("stats.saving", "true");
            properties.setProperty("stats.type", "dir");
            properties.setProperty("stats.db", "players");
            properties.setProperty("stats.ip", "");
            properties.setProperty("stats.port", "");
            properties.setProperty("stats.user", "");
            properties.setProperty("stats.pass", "");

            properties.store(outputStream, "Server Configuration");
            logger.info("Default config file created successfully.");
            loadConfig();
        } catch (IOException e) {
            logger.error("Error while creating default config file: " + e.getMessage());
        }
    }

    static String getServerIp() {
        return properties.getProperty("server.ip");
    }
    static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }
    static String getServerMode() {
        return properties.getProperty("server.mode");
    }
    static int getServerSlots() {
        return Integer.parseInt(properties.getProperty("server.slots"));
    }
    static String getProxySecret() {
        return properties.getProperty("server.proxysecret");
    }
    static int getGameMinSlots() {
        return Integer.parseInt(properties.getProperty("game.minslots"));
    }
    static int getGameMaxSlots() {
        return Integer.parseInt(properties.getProperty("game.maxslots"));
    }
    static int getQueueWaitTime() {
        return Integer.parseInt(properties.getProperty("game.waittime"));
    }
    static boolean getQueueAllowForce() {
        return Boolean.parseBoolean(properties.getProperty("game.allowforce"));
    }
    static boolean getStatsEnabled() {
        return Boolean.parseBoolean(properties.getProperty("stats.saving"));
    }
    static String getStatsType() {
        return properties.getProperty("stats.type");
    }
    static String getStatsIp() {
        return properties.getProperty("stats.ip");
    }
    static int getStatsPort() {
        return Integer.parseInt(properties.getProperty("stats.port"));
    }
    static String getStatsDb() {
        return properties.getProperty("stats.db");
    }
    static String getStatsUser() {
        return properties.getProperty("stats.user");
    }
    static String getStatsPass() {
        return properties.getProperty("stats.pass");
    }
}