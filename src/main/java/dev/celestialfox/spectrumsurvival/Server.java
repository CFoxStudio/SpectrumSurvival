package dev.celestialfox.spectrumsurvival;

import dev.celestialfox.spectrumsurvival.game.commands.*;
import dev.celestialfox.spectrumsurvival.game.managers.GameManager;
import dev.celestialfox.spectrumsurvival.game.managers.QueueManager;
import dev.celestialfox.spectrumsurvival.utils.classes.Randomized;
import dev.celestialfox.spectrumsurvival.utils.config.Checks;
import dev.celestialfox.spectrumsurvival.utils.config.Settings;
import dev.celestialfox.spectrumsurvival.utils.events.ConnectionEvents;
import dev.celestialfox.spectrumsurvival.utils.events.MiscEvents;
import dev.celestialfox.spectrumsurvival.utils.events.StartEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandManager;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    // Other
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        // Server
        logger.info("{ Initializing Server }");
        MinecraftServer server = MinecraftServer.init();

        // Files
        logger.info("{ File Checks (Config, Worlds) }");
        Checks.configFile();
        Checks.worldFiles();

        // Events
        logger.info("{ Startup Events (Chat, Spawn, etc.) }");
        StartEvents.registerChat();
        StartEvents.handleSpawn();
        logger.info("{ Miscellaneous Events (Game Listeners, Commands, etc.) }");
        MiscEvents.register();
        ConnectionEvents.register();
        registerCommands();

        Terminal.start();
        tablist();

        // Config checks
        logger.info("{ Config File Checks }");
        Checks.slots();
        Checks.mode();
        Checks.gameSettings();

        // Server Start
        MinecraftServer.setBrandName("Spectrum Survival");
        server.start(Settings.getIP(), Settings.getPort());
        logger.info("Server Started at %s:%s (MC: %s)".formatted(Settings.getIP(), Settings.getPort(), MinecraftServer.VERSION_NAME));
    }

    public static void registerCommands() {
        logger.info("Registering Commands");
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new QueueCommand());
        commandManager.register(new AboutCommand());
        commandManager.register(new CreditsCommand());
        commandManager.register(new StopCommand());
    }

    public static void tablist() {
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            Component header = Component.text("\nᴘʟᴀʏᴇʀѕ ᴏɴʟɪɴᴇ: " + MinecraftServer.getConnectionManager().getOnlinePlayers().size(), NamedTextColor.BLUE)
                    .append(Component.text("\nᴘʟᴀʏᴇʀѕ ɪɴ ǫᴜᴇᴜᴇ: " + QueueManager.getPlayersInQueue(), NamedTextColor.GRAY)
                    .append(Component.text("\nᴘʟᴀʏᴇʀѕ ɪɴ-ɢᴀᴍᴇ: " + GameManager.getPlayersInGame() + "\n", NamedTextColor.BLUE)));

            Component footer = Component.newline()
                    .append(Component.text("ᴍᴀᴅᴇ ʙʏ: CelestialFox Studio", Randomized.madeByColor()));

            Audiences.players().sendPlayerListHeaderAndFooter(header, footer);
        }, TaskSchedule.tick(10), TaskSchedule.tick(10));
    }
}