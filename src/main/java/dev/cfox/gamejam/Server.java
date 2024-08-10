package dev.celestialfox.gamejam;

import dev.celestialfox.gamejam.utils.Checks;
import dev.celestialfox.gamejam.utils.config.Settings;
import dev.celestialfox.gamejam.utils.events.StartEvents;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
    // Other
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        // Server
        logger.info("Initializing Server");
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Files
        Checks.worldFiles();
        Checks.configFile();

        // Events
        logger.info("Setting Up - Startup Events (Chat, Spawn, etc.)");
        StartEvents.registerChat();
        StartEvents.handleSpawn();

        // Server Start
        minecraftServer.start(Settings.getIP(), Settings.getPort());
        logger.info("Server Started!");
    }
}