package dev.celestialfox.spectrumsurvival.game.managers;

import dev.celestialfox.spectrumsurvival.game.classes.GameLobby;
import dev.celestialfox.spectrumsurvival.game.classes.GameQueue;
import dev.celestialfox.spectrumsurvival.utils.Misc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConversionManager.class);
    public static GameLobby toGame(GameQueue queue) {
        GameLobby game = new GameLobby();
        game.setPlayers(queue.getPlayers());
        game.genName();
        GameManager.gameLobbies.put(game.getName(), game);
        logger.debug("New GameLobby created: " + game.getName());

        return game;
    }

    public static void fromGame(GameLobby game) {
        if (!GameManager.gameLobbies.containsKey(game.getName())) {
            return;
        }

        logger.debug("GameLobby removed: " + game.getName());
        game.sendMessage(Component.text("Game ended!", NamedTextColor.RED));
        game.setInstance(QueueManager.lobbyInstance, new Pos(0, 66, 0, 180, 0));
        game.getPlayers().forEach(uuid -> {
            Misc.getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
            Misc.getPlayer(uuid).setHealth(20);
        });
        GameManager.sendEndTitles(game);
        GameManager.gameLobbies.remove(game.getName());
    }
}
