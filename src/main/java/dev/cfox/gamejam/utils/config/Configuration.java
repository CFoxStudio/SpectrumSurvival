package dev.celestialfox.gamejam.utils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    public static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();

    public static void loadConfig() {
        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
            logger.info("Config file loaded successfully.");
        } catch (IOException e) {
            logger.error("Error while loading config file: " + e.getMessage());
        }
    }

    public static void createDefaultConfig() {
        try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
            properties.setProperty("server.ip", "0.0.0.0");
            properties.setProperty("server.port", "25565");

            properties.store(outputStream, "Server Configuration");
            logger.info("Default config file created successfully.");
        } catch (IOException e) {
            logger.error("Error while creating default config file: " + e.getMessage());
        }
        loadConfig();
    }

    static String getServerIp() {
        return properties.getProperty("server.ip");
    }
    static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }
}