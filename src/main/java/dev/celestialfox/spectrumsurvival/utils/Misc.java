package dev.celestialfox.spectrumsurvival.utils;

import dev.celestialfox.spectrumsurvival.game.classes.GameLobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.UUID;

public class Misc {
    static Task timeTask;

    public static Player getPlayer(UUID uuid) {
        return MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid);
    }

    public static void showTitle(Instance instance, Component title, Component subtitle) {
        for (Player player : instance.getPlayers()) { // Get all players in the instance
            player.showTitle(Title.title(title, subtitle)); // Send the title to each player
        }
    }

    public static void replaceInRegion(Instance instance, Pos start, Pos end, Block newBlockType) {
        int startX = (int) Math.min(start.x(), end.x());
        int startY = (int) Math.min(start.y(), end.y());
        int startZ = (int) Math.min(start.z(), end.z());

        int endX = (int) Math.max(start.x(), end.x());
        int endY = (int) Math.max(start.y(), end.y());
        int endZ = (int) Math.max(start.z(), end.z());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Pos pos = new Pos(x, y, z);
                    if (instance.getBlock(pos).compare(Block.AIR)
                            || instance.getBlock(pos).compare(Block.MOSS_CARPET)
                            || instance.getBlock(pos).compare(Block.SHORT_GRASS)
                            || instance.getBlock(pos).compare(Block.TALL_GRASS)
                            || instance.getBlock(pos).compare(Block.WHEAT)
                            || instance.getBlock(pos).compare(Block.CREEPER_HEAD)
                            || instance.getBlock(pos).compare(Block.RED_CARPET)
                            || instance.getBlock(pos).compare(Block.WHITE_CARPET)) {
                        instance.setBlock(pos, newBlockType);
                    }
                }
            }
        }
    }

    public static void transitionToTime(GameLobby game, Duration duration, long startTime, long endTime) {
        long transitionDuration = duration.toMillis();
        long startMillis = System.currentTimeMillis();

        timeTask = MinecraftServer.getSchedulerManager().buildTask(() -> {
            long currentMillis = System.currentTimeMillis();
            long elapsedMillis = currentMillis - startMillis;
            double progress = Math.min(1.0, (double) elapsedMillis / transitionDuration);

            long newTime = startTime + (long) (progress * (endTime - startTime));
            game.getInstance().setTime(newTime);

            if (progress >= 1.0) {
                game.getInstance().setTime(endTime);
                timeTask.cancel();
            }
        }).repeat(Duration.ofMillis(50)).schedule();
    }

    public static Block getBlockAtPlayerPosition(Player player) {
        Pos playerPosition = player.getPosition();
        Instance instance = player.getInstance();
        return instance.getBlock(playerPosition);
    }
}
