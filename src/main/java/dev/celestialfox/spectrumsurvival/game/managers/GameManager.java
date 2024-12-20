package dev.celestialfox.spectrumsurvival.game.managers;

import dev.celestialfox.spectrumsurvival.Server;
import dev.celestialfox.spectrumsurvival.game.phases.PhaseLogic;
import dev.celestialfox.spectrumsurvival.utils.Misc;
import dev.celestialfox.spectrumsurvival.game.classes.GameLobby;
import dev.celestialfox.spectrumsurvival.game.classes.GameQueue;
import dev.celestialfox.spectrumsurvival.utils.config.StatsSettings;
import net.hollowcube.polar.PolarLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class GameManager {
    public static Map<String, GameLobby> gameLobbies = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    public static boolean isPlayerInGame(Player player) {
        for (GameLobby lobby : gameLobbies.values()) {
            if (lobby.getPlayers().contains(player.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public static void startGame(GameQueue queue) {
        queue.sendMessage(Component.text("The game is starting!", NamedTextColor.GREEN, TextDecoration.BOLD));
        GameLobby game = ConversionManager.toGame(queue);
        startGame(game);
        QueueManager.removeQueue(queue);
    }

    public static GameLobby getPlayerGame(Player player) {
        if (!gameLobbies.isEmpty()) {
            for (GameLobby gameLobby : gameLobbies.values()) {
                if (gameLobby.getPlayers().contains(player.getUuid())) {
                    return gameLobby;
                }
            }
        }
        return null;
    }

    public static GameLobby getGame(String lobby) {
        return gameLobbies.get(lobby);
    }

    public static void startGame(GameLobby game) {
        logger.debug("Creating Game Instance");
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        try {
            instanceContainer.setChunkLoader(new PolarLoader(Path.of("worlds/game.polar")));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        game.setInstance(instanceContainer, new Pos(0, 66, 0));
        for (UUID uuid : game.getPlayers()) {
            Misc.getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
        }
        PhaseLogic.random(game);
    }

    public static void sendEndTitles(GameLobby game) {
        List<UUID> uuids = game.getPlayers();
        List<Player> playersWon = new ArrayList<>();
        List<Player> playersLost = new ArrayList<>();
        List<String> winners = new ArrayList<>();
        List<UUID> eliminated = game.getEliminated();

        for (UUID uuid : uuids) {
            if (eliminated.contains(uuid)) {
                Misc.getPlayer(uuid).showTitle(Title.title(Component.text("You Lost.", NamedTextColor.RED, TextDecoration.BOLD), Component.text("")));
                playersLost.add(Misc.getPlayer(uuid));
            } else {
                Misc.getPlayer(uuid).showTitle(Title.title(Component.text("You Win!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.text("")));
                playersWon.add(Misc.getPlayer(uuid));
                winners.add(Misc.getPlayer(uuid).getUsername());
            }
        }

        // Announce winners
        game.sendMessage(Component.text("Winner(s): " + winners, NamedTextColor.YELLOW));

        // Update Stats
        if (StatsSettings.getEnabled()) {
            Server.stats.won(playersWon);
            Server.stats.lost(playersLost);
        }
    }

    public static int getPlayersInGame() {
        int totalPlayers = 0;
        for (GameLobby gameLobby : gameLobbies.values()) {
            totalPlayers += gameLobby.getPlayers().size();
        }
        return totalPlayers;
    }
}
