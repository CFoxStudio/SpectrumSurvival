package dev.cfox.gamejam.game.managers;

import dev.cfox.gamejam.game.classes.GameLobby;
import dev.cfox.gamejam.game.classes.GameQueue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
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
        game.setInstance(QueueManager.lobbyInstance, new Pos(0, 60, 0, 180, 0));
        GameManager.gameLobbies.remove(game.getName());
    }
}
