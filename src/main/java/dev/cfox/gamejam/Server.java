package dev.cfox.gamejam;

import dev.cfox.gamejam.utils.config.Checks;
import dev.cfox.gamejam.utils.config.Settings;
import dev.cfox.gamejam.utils.events.StartEvents;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    // Other
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        // Server
        logger.info("Initializing Server");
        MinecraftServer server = MinecraftServer.init();

        // Files
        logger.info("File Checks (Config, Worlds)");
        Checks.configFile();
        Checks.worldFiles();

        // Events
        logger.info("Startup Events (Chat, Spawn, etc.)");
        StartEvents.registerChat();
        StartEvents.handleSpawn();

        // Server Start
        server.start(Settings.getIP(), Settings.getPort());
        logger.info("Server Started at " + Settings.getIP() + ":" + Settings.getPort() + " (MC: " + MinecraftServer.VERSION_NAME + ")");
    }
}