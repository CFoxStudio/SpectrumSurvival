package dev.celestialfox.gamejam.utils.events;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartEvents {
    static GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
    private static final Logger logger = LoggerFactory.getLogger(StartEvents.class);


    public static void registerChat() {
        logger.debug("Registering Chat Listener");
        globalEventHandler.addListener(PlayerChatEvent.class, event -> {
            event.setCancelled(true);

            // Get everything
            String username = event.getPlayer().getUsername();
            Instance instance = event.getInstance();
            String message = event.getMessage();

            // Send message to player's instance
            instance.sendMessage(Component.text("§e" + username + "§7 ･ " + message));
        });
    }
    public static void handleSpawn() {
        logger.debug("Registering Spawn Listener");
        InstanceContainer instanceContainer = createSpawnInstance();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });
    }
    public static InstanceContainer createSpawnInstance() {
        logger.debug("Creating Spawn Instance");
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        instanceContainer.setChunkSupplier(LightingChunk::new);

        return instanceContainer;
    }
}
