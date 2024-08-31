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

    public static boolean isFlamableBlock(Block block) {
        return !block.compare(Block.AIR)
                && !block.compare(Block.GRASS_BLOCK)
                && !block.compare(Block.FARMLAND)
                && !block.compare(Block.STONE)
                && !block.compare(Block.ANDESITE)
                && !block.compare(Block.ANDESITE_STAIRS)
                && !block.compare(Block.ANDESITE_SLAB)
                && !block.compare(Block.GRAVEL)
                && !block.compare(Block.DIRT_PATH)
                && !block.compare(Block.OAK_FENCE)
                && !block.compare(Block.OAK_FENCE_GATE)
                && !block.compare(Block.STONE_BRICKS)
                && !block.compare(Block.MOSSY_STONE_BRICK_WALL)
                && !block.compare(Block.MOSSY_STONE_BRICKS)
                && !block.compare(Block.MOSSY_STONE_BRICK_STAIRS)
                && !block.compare(Block.STONE_BRICK_STAIRS)
                && !block.compare(Block.MOSSY_STONE_BRICK_SLAB)
                && !block.compare(Block.STONE_BRICK_WALL)
                && !block.compare(Block.BRICK_SLAB)
                && !block.compare(Block.BRICK_STAIRS)
                && !block.compare(Block.CHAIN)
                && !block.compare(Block.WATER_CAULDRON)
                && !block.compare(Block.OAK_TRAPDOOR)
                && !block.compare(Block.OAK_PLANKS)
                && !block.compare(Block.SPRUCE_FENCE)
                && !block.compare(Block.SPRUCE_FENCE_GATE)
                && !block.compare(Block.SPRUCE_TRAPDOOR)
                && !block.compare(Block.SPRUCE_WALL_SIGN)
                && !block.compare(Block.SPRUCE_STAIRS)
                && !block.compare(Block.BARRIER)
                && !block.compare(Block.SAND)
                && !block.compare(Block.MOSS_BLOCK)
                && !block.compare(Block.WATER)
                && !block.compare(Block.LILY_PAD)
                && !block.compare(Block.SEA_LANTERN)
                && !block.compare(Block.RED_CARPET)
                && !block.compare(Block.WHITE_CARPET)
                && !block.compare(Block.TRIPWIRE);
    }
}
