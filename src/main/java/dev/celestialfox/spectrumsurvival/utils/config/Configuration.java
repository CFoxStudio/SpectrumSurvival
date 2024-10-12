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
}