package dev.cfox.gamejam.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

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
}
