package dev.cfox.gamejam.game.classes;

import dev.cfox.gamejam.game.managers.QueueManager;
import dev.cfox.gamejam.utils.Misc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class GameQueue {
    private final ArrayList<UUID> players = new ArrayList<>();

    public void addPlayer(UUID uuid) {
        if (QueueManager.getPlayerQueue(Misc.getPlayer(uuid)) == null) {
            players.add(uuid);
            Misc.getPlayer(uuid).setGameMode(GameMode.SPECTATOR);
            Misc.getPlayer(uuid).sendMessage(Component.text("You've joined a queue!", NamedTextColor.GREEN));
        } else {
            Misc.getPlayer(uuid).sendMessage(Component.text("You're already in a queue!", NamedTextColor.RED));
        }
    }

    public void removePlayer(UUID uuid) {
        if (players.contains(uuid)) {
            players.remove(uuid);
            Misc.getPlayer(uuid).setGameMode(GameMode.SURVIVAL);
            Misc.getPlayer(uuid).sendMessage(Component.text("You have left the queue", NamedTextColor.RED));
        }
    }

    public ArrayList<UUID> getPlayers() {
        return players;
    }

    public void sendMessage(Component component) {
        players.forEach(uuid -> Misc.getPlayer(uuid).sendMessage(component));
    }
}
