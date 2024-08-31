package dev.celestialfox.spectrumsurvival.utils.events;

import dev.celestialfox.spectrumsurvival.game.classes.GameLobby;
import dev.celestialfox.spectrumsurvival.game.classes.SnowballProjectile;
import dev.celestialfox.spectrumsurvival.game.classes.ZombieCreature;
import dev.celestialfox.spectrumsurvival.game.managers.GameManager;
import dev.celestialfox.spectrumsurvival.game.managers.QueueManager;
import dev.celestialfox.spectrumsurvival.game.phases.Phase;
import dev.celestialfox.spectrumsurvival.utils.Misc;
import dev.celestialfox.spectrumsurvival.utils.classes.NPC;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithEntityEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MiscEvents {
    private static final double KNOCKBACK_STRENGTH = 15.0;
    static GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
    private static final Logger logger = LoggerFactory.getLogger(MiscEvents.class);

    public static void register() {
        logger.debug("Registering Misc Listeners");
        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            if (GameManager.isPlayerInGame(player)) {
                if (GameManager.getPlayerGame(player).getPhase() == Phase.GREEN) {
                    if (Misc.getBlockAtPlayerPosition(player) == Block.WITHER_ROSE && player.getGameMode() == GameMode.ADVENTURE) {
                        player.damage(Damage.fromPlayer(player, 1));
                    }
                } else if (GameManager.getPlayerGame(player).getPhase() == Phase.RED) {
                    if (Misc.getBlockAtPlayerPosition(player) == Block.FIRE && player.getGameMode() == GameMode.ADVENTURE) {
                        player.damage(Damage.fromPosition(DamageType.IN_FIRE, player.getPosition(), 1));
                    }
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

                if (targeted instanceof Player player2) {
                    if (player.getGameMode() == GameMode.ADVENTURE) {
                        applyKnockback(player, player2);
                        if (GameManager.isPlayerInGame(player)) {
                            if (GameManager.getPlayerGame(player).getPhase() == Phase.GRAY) {
                                player2.damage(Damage.fromPlayer(player, 1));
                            }
                        }
                    }
                }
            }

            if (targeted instanceof Player player) {
                if (GameManager.isPlayerInGame(player)) {
                    if (GameManager.getPlayerGame(player).getPhase() == Phase.GREEN) {
                        if (targeter instanceof ZombieCreature) {
                            ZombieCreature zombie = new ZombieCreature();
                            zombie.setInstance(player.getInstance(), player.getPosition());
                            GameManager.getPlayerGame(player).eliminate(player);
                        }
                    }
                }
            }
        });

        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            GameLobby playerGame = GameManager.getPlayerGame(player);
            UUID uuid = player.getUuid();
            if (GameManager.isPlayerInGame(player)) {
                playerGame.getPlayers().remove(uuid);
                playerGame.getEliminated().remove(uuid);
            }
            if (QueueManager.isPlayerInQueue(player)) {
                QueueManager.getPlayerQueue(player).removePlayer(uuid);
            }
        });

        globalEventHandler.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            ItemStack item = event.getItemStack();

            if (item.material() == Material.SNOWBALL) {
                Pos playerPosition = player.getPosition().add(0, 1.5, 0);
                Vec direction = player.getPosition().direction().normalize().mul(25);
                SnowballProjectile snowball = new SnowballProjectile(player);

                snowball.setInstance(player.getInstance(), playerPosition);
                snowball.setVelocity(direction);
                snowball.setNoGravity(false);
                snowball.spawn();

                MinecraftServer.getSchedulerManager().buildTask(() -> {
                    if (snowball.isActive()) {
                        snowball.remove();
                    }
                }).delay(TaskSchedule.seconds(10)).schedule();
                event.setCancelled(true);
            }
        });

        globalEventHandler.addListener(ProjectileCollideWithBlockEvent.class, event -> {
            if (event.getEntity() instanceof SnowballProjectile snowball) {
                if (snowball.isActive()) {
                    snowball.remove();
                }
            }
        });

        globalEventHandler.addListener(ProjectileCollideWithEntityEvent.class, event -> {
            if (event.getEntity() instanceof SnowballProjectile snowball) {
                if (event.getTarget() instanceof Player target) {
                    if (snowball.isActive()) {
                        snowball.remove();
                    }
                    target.damage(Damage.fromProjectile(snowball, snowball, 0.75F));
                }
            }
        });

        globalEventHandler.addListener(PlayerDeathEvent.class, event -> {
            // Cancel the default death message
            event.setDeathText(Component.text("You were eliminated!"));
            event.setChatMessage(null);
            if (GameManager.isPlayerInGame(event.getPlayer())) {
                GameManager.getPlayerGame(event.getPlayer()).eliminate(event.getPlayer());
            }
        });

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                if (GameManager.isPlayerInGame(player)
                        && !(GameManager.getPlayerGame(player).getPhase() == Phase.GRAY)
                        && player.getHealth() != 20) {
                    player.setHealth(player.getHealth()+1);
                }
            }
        }).repeat(TaskSchedule.seconds(2)).schedule();
    }

    private static void applyKnockback(Player attacker, Player target) {
        Pos targetPos = target.getPosition();
        Pos attackerPos = attacker.getPosition();
        double dx = targetPos.x() - attackerPos.x();
        double dz = targetPos.z() - attackerPos.z();
        double length = Math.sqrt(dx * dx + dz * dz);

        if (length > 0) {
            dx /= length;
            dz /= length;
            target.setVelocity(target.getVelocity().add(dx * KNOCKBACK_STRENGTH, 0, dz * KNOCKBACK_STRENGTH));
        }
    }
}
