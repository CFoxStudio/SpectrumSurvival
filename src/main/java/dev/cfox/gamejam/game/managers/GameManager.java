package dev.cfox.gamejam.game.managers;

import dev.cfox.gamejam.game.classes.GameLobby;
import dev.cfox.gamejam.game.classes.GameQueue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.*;

public class GameManager {
    public static Map<String, GameLobby> gameLobbies = new HashMap<>();

    public static boolean isPlayerInGame(Player player) {
        for (GameLobby lobby : gameLobbies.values()) {
            if (lobby.getAllPlayers().contains(player.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public static void startGame(GameQueue queue) {
        queue.sendMessage(Component.text("The game is starting!", NamedTextColor.GREEN, TextDecoration.BOLD));
//        String game = LobbyConverter.toGame(queue);
        QueueManager.removeQueue(queue);
    }

    public static GameLobby getPlayerGame(Player player) {
        if (!gameLobbies.isEmpty()) {
            for (GameLobby gameLobby : gameLobbies.values()) {
                if (gameLobby.getAllPlayers().contains(player.getUuid())) {
                    return gameLobby;
                }
            }
        }
        return null;
    }

    public static GameLobby getGame(String lobby) {
        return gameLobbies.get(lobby);
    }

    public static int getPlayersInGame() {
        int totalPlayers = 0;
        for (GameLobby gameLobby : gameLobbies.values()) {
            totalPlayers += gameLobby.getAllPlayers().size();
        }
        return totalPlayers;
    }

    public static void sendEndTitles(GameLobby game) {
        Instance instance = game.getInstance();
        List<String> winners = new ArrayList<>();
        List<UUID> eliminated = game.getEliminated();

        for (Player player : instance.getPlayers()) {
            if (eliminated.contains(player.getUuid())) {
                player.showTitle(Title.title(Component.text("Eliminated!", NamedTextColor.RED, TextDecoration.BOLD), Component.text("")));
            } else {
                // Player is a winner if they were not eliminated
                player.showTitle(Title.title(Component.text("Qualified!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text("")));
                winners.add(player.getUsername());
            }
        }

        // Announce winners
        instance.sendMessage(Component.text("Winner(s): " + winners, NamedTextColor.YELLOW));
    }
}
