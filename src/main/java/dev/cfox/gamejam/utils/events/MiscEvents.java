package dev.cfox.gamejam.utils.events;

import dev.cfox.gamejam.game.managers.GameManager;
import dev.cfox.gamejam.game.managers.QueueManager;
import dev.cfox.gamejam.game.phases.Phase;
import dev.cfox.gamejam.utils.classes.NPC;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscEvents {
    static GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
    private static final Logger logger = LoggerFactory.getLogger(MiscEvents.class);

    public static void register() {
        logger.debug("Registering Misc Listeners");
        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            if (GameManager.isPlayerInGame(player)) {
                Phase phase = GameManager.getPlayerGame(player).getPhase();

                if (phase == Phase.YELLOW) {

                }
            }
        });

        globalEventHandler.addListener(EntityAttackEvent.class, event -> {
            Entity targeter = event.getEntity();
            Entity targeted = event.getTarget();

            if (targeter instanceof Player player) {
                if (targeted instanceof NPC npc && npc.getCustomName().equals(Component.text("Join the Queue"))) {
                    QueueManager.joinPlayer(player);
                }
            }
        });
    }
}
