package dev.cfox.gamejam.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.UUID;

public class Misc {
    public static Player getPlayer(UUID uuid) {
        return MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid);
    }

    public static void showTitle(Instance instance, Component title, Component subtitle) {
        for (Player player : instance.getPlayers()) { // Get all players in the instance
            player.showTitle(Title.title(title, subtitle)); // Send the title to each player
        }
    }

    public void replaceAirInRegion(Instance instance, Pos start, Pos end, Block newBlockType) {
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
                    if (instance.getBlock(pos).compare(Block.AIR)) {
                        instance.setBlock(pos, newBlockType);
                    }
                }
            }
        }
    }
}
