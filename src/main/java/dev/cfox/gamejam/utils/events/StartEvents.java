package dev.cfox.gamejam.utils.events;

import dev.cfox.gamejam.utils.Misc;
import dev.cfox.gamejam.utils.classes.NPC;
import dev.cfox.gamejam.utils.classes.Randomized;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.*;
import net.minestom.server.instance.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

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
        InstanceContainer instanceContainer = createLobbyInstance();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();

            if (event.isFirstSpawn()) {
                var mm = MiniMessage.miniMessage();
                Component minestom = mm.deserialize("<gradient:#5e4fa2:#f79459:red>Minestom NPC</gradient>");

                player.sendMessage(Component.text(
                        "§e§lClick on §ror §e§lrun into §rthe ", NamedTextColor.GRAY).append(minestom).append(Component.text(" to §a§ljoin the queue!")));
                Misc.showTitle(player.getInstance(), Component.text("■", NamedTextColor.RED), Component.text("Red", NamedTextColor.RED));
            }
        });
    }
    public static InstanceContainer createLobbyInstance() {
        logger.debug("Creating Lobby Instance");
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
//        try {
//            instanceContainer.setChunkLoader(new PolarLoader(Path.of("worlds/lobby.polar")));
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        instanceContainer.setChunkSupplier(LightingChunk::new);

        NPC joinNPC = new NPC("Join the Queue",
                "ewogICJ0aW1lc3RhbXAiIDogMTY3MjE3MTU1MjU1OCwKICAicHJvZmlsZUlkIiA6ICJiNTAwOGEyMGJkY2U0YjJlOTU5NWZlNzY2MDlmYjUzNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNT1JJU3hTVE9QQkFOIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RmZDIwOWU5ZTk2MGY4MDQ5ZDdlYWY0NTJmYzg1YmYwYmVjZjliMTUwYjQ2MzIwMTY2Mjg1YzdhYjEzMjMxN2UiCiAgICB9CiAgfQp9",
                "yWIH6gVDRhgd/HwVYpRT87NBHf+q9EgYs/CYqIwnz5xXR22k29c7MTnaYFvWukbwF1FhWNilAyYzagSn/7ScvOpYn0RtyTWk656cwjzFSbTsSFzRwUl5f4mu1EeunYh8v5cZH27KTfAI7a9Q7ylXz7NoAbvaw056thXa7jHhLhFdPECnziRTnv9jDRwpoN/4jblmdOz5NCLtynubf8hwIwm9od18tXy4+gsV3aXS5+1MirpWDizqdozb9mtwzML9NYwVNpO2bRB9KYJ91VUWqxjfTy/q0xFQ1paolq4pp3tgvLXw0y+rdwCsCgh39JA4MKvIIJShww5xbqo4oFBRDj+/BI3+Y154Ess1004vE+iTRdt+az0v4y+evnOQLgryEr/36QzZOndpSFqYfKPl1MeUeZe1u4VDQJcgyJImg2TZJbG2WOmmTySWSEPrHcYC6c3Y9rVnQ6Zi4NxTe4e6/ZuDQVm14fuSUPd4Ll7/aIDyumHupBMMbBEa9qCmuZJPT5iWVlIGfzA2Dg/kea4Jw9WudUmiCYngB56HZEivDPniIxeGSTRFHMR2FfTKnLkxb2LDOvD+CgDWyr8cGy4xnB2hwdY2n28cCAYI5axj0qzCpHMl8Y90e2rKfX7NsUvyivbAVRwAsd/bWkkJoZ4/QStFEjV//81iNbuUz/4lFQA=");
        joinNPC.setInstance(instanceContainer, new Pos(0, 40, 0));

        return instanceContainer;
    }
}
