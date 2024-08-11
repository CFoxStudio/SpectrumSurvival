package dev.cfox.gamejam.game.phases;

import dev.cfox.gamejam.game.classes.GameLobby;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class PhaseLogic {
    private static final Logger logger = LoggerFactory.getLogger(PhaseLogic.class);

    public static void random(GameLobby game) {
        Phase[] phases = Phase.values();
        int randomIndex = new Random().nextInt(phases.length);
        Phase selectedPhase = phases[randomIndex];
        logger.info("Picking random color phase for GameLobby: " + game.getName());

        switch (selectedPhase) {
            case RED -> red(game);
            case BLUE -> blue(game);
            case GREEN -> green(game);
            case YELLOW -> yellow(game);
            case PURPLE -> purple(game);
        }
    }

    public static void red(GameLobby game) {
        // Handle red phase logic
    }
    public static void blue(GameLobby game) {
        // Handle blue phase logic
    }
    public static void green(GameLobby game) {
        // Handle green phase logic
    }
    public static void yellow(GameLobby game) {
        // Handle yellow phase logic
    }
    public static void purple(GameLobby game) {
        // Handle purple phase logic
    }
}
