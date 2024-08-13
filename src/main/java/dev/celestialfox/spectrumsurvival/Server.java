package dev.celestialfox.spectrumsurvival;

import dev.celestialfox.spectrumsurvival.game.commands.*;
import dev.celestialfox.spectrumsurvival.utils.config.Checks;
import dev.celestialfox.spectrumsurvival.utils.config.Settings;
import dev.celestialfox.spectrumsurvival.utils.events.MiscEvents;
import dev.celestialfox.spectrumsurvival.utils.events.StartEvents;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
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
        logger.info("Miscellaneous Events (Game Listeners, Commands, etc.)");
        MiscEvents.register();
        registerCommands();

        Terminal.start();

        // Server Start
        server.start(Settings.getIP(), Settings.getPort());
        logger.info("Server Started at " + Settings.getIP() + ":" + Settings.getPort() + " (MC: " + MinecraftServer.VERSION_NAME + ")");
    }

    public static void registerCommands() {
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new QueueCommand());
        commandManager.register(new AboutCommand());
        commandManager.register(new CreditsCommand());
        commandManager.register(new StopCommand());
    }
}