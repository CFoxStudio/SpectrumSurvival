package dev.celestialfox.spectrumsurvival.utils.events;

import dev.celestialfox.spectrumsurvival.utils.config.Settings;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionEvents {
    static GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
    private static final Logger logger = LoggerFactory.getLogger(MiscEvents.class);

    public static void register() {
        globalEventHandler.addListener(AsyncPlayerPreLoginEvent.class, event -> {
            if (!(Settings.getSlots() <= 0) && MinecraftServer.getConnectionManager().getOnlinePlayerCount() == Settings.getSlots()) {
                event.getConnection().getPlayer().kick("The server is full");
                logger.error("Player '%s' was kicked because the server is full (%s)"
                        .formatted(event.getConnection().getPlayer().getUsername(), (MinecraftServer.getConnectionManager().getOnlinePlayerCount() + "/" + Settings.getSlots())));
            }
        });

        globalEventHandler.addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            // Description (MOTD)
            responseData.setDescription(Component.text("Spectrum Survival by CelestialFox"));

            // Server Slots
            if (!(Settings.getSlots() <= 0)) {
                responseData.setMaxPlayer(Settings.getSlots());
            }
        });
    }
}
