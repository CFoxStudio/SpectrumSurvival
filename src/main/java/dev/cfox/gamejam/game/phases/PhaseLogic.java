package dev.cfox.gamejam.game.phases;

import dev.cfox.gamejam.game.classes.GameLobby;
import dev.cfox.gamejam.game.managers.ConversionManager;
import dev.cfox.gamejam.utils.Misc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PhaseLogic {
    private static final Logger logger = LoggerFactory.getLogger(PhaseLogic.class);
    private static final SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
    private static int phaseDuration = 30; // in seconds
    private static int gameTime = 300; // 5 minutes in seconds

    public static void random(GameLobby game) {
        Task task = scheduler.buildTask(() -> {
            Phase[] phases = Phase.values();
            int randomIndex = new Random().nextInt(phases.length);
            Phase selectedPhase = phases[randomIndex];
            if (game.getTask() != null) {
                game.getTask().cancel();
            }
            switch (selectedPhase) {
                case RED -> red(game);
                case BLUE -> blue(game);
                case GREEN -> green(game);
                case YELLOW -> yellow(game);
                case PURPLE -> purple(game);
            }
        }).repeat(TaskSchedule.seconds(phaseDuration)).schedule();
        scheduler.buildTask(() -> {
            ConversionManager.fromGame(game);
            task.cancel();
        }).delay(TaskSchedule.seconds(gameTime)).schedule();
    }

    public static void red(GameLobby game) {
        logger.debug("Picked red phase for GameLobby: " + game.getName());
        game.setPhase(Phase.RED);
        game.sendMessage(Component.text("New color picked! §cRED", NamedTextColor.GRAY));
        game.sendMessage(Component.text("BLAH BLAH BLAH BLAH", NamedTextColor.RED));
        Misc.showTitle(game.getInstance(), Component.text("■", NamedTextColor.RED), Component.text("Red", NamedTextColor.RED));
        // Handle red phase logic
    }
    public static void blue(GameLobby game) {
        logger.debug("Picked blue phase for GameLobby: " + game.getName());
        game.setPhase(Phase.BLUE);
        game.sendMessage(Component.text("New color picked! §9BLUE", NamedTextColor.GRAY));
        game.sendMessage(Component.text("BLAH BLAH BLAH BLAH", NamedTextColor.BLUE));
        Misc.showTitle(game.getInstance(), Component.text("■", NamedTextColor.BLUE), Component.text("Blue", NamedTextColor.BLUE));
        // Handle blue phase logic
    }
    public static void green(GameLobby game) {
        logger.debug("Picked green phase for GameLobby: " + game.getName());
        game.setPhase(Phase.GREEN);
        game.sendMessage(Component.text("New color picked! §aGREEN", NamedTextColor.GRAY));
        game.sendMessage(Component.text("BLAH BLAH BLAH BLAH", NamedTextColor.GREEN));
        Misc.showTitle(game.getInstance(), Component.text("■", NamedTextColor.GREEN), Component.text("Green", NamedTextColor.GREEN));
        // Handle green phase logic
    }
    public static void yellow(GameLobby game) {
        logger.debug("Picked yellow phase for GameLobby: " + game.getName());
        game.setPhase(Phase.YELLOW);
        game.sendMessage(Component.text("New color picked! §eYELLOW", NamedTextColor.GRAY));
        game.sendMessage(Component.text("Thunder is coming! Find a place to hide!", NamedTextColor.YELLOW));
        Misc.showTitle(game.getInstance(), Component.text("■", NamedTextColor.YELLOW), Component.text("Yellow", NamedTextColor.YELLOW));
        scheduler.buildTask(() -> {
            Task task = scheduler.buildTask(() -> {
                List<UUID> uuids = game.getPlayers();
                Random random = new Random();
                Player randomPlayer = null;

                while (randomPlayer == null || randomPlayer.getGameMode() != GameMode.ADVENTURE) {
                    randomPlayer = Misc.getPlayer(uuids.get(random.nextInt(uuids.size())));
                }

                Entity lightning = new Entity(EntityType.LIGHTNING_BOLT);
                lightning.setInstance(randomPlayer.getInstance(), randomPlayer.getPosition());
                lightning.spawn();
                game.eliminate(randomPlayer);
            }).repeat(Duration.ofSeconds(5)).schedule();
            game.setTask(task);
        }).delay(Duration.ofSeconds(5)).schedule();
    }
    public static void purple(GameLobby game) {
        logger.debug("Picked purple phase for GameLobby: " + game.getName());
        game.setPhase(Phase.PURPLE);
        game.sendMessage(Component.text("New color picked! §dPURPLE", NamedTextColor.GRAY));
        game.sendMessage(Component.text("BLAH BLAH BLAH BLAH", NamedTextColor.LIGHT_PURPLE));
        Misc.showTitle(game.getInstance(), Component.text("■", NamedTextColor.LIGHT_PURPLE), Component.text("Purple", NamedTextColor.LIGHT_PURPLE));
        // Handle purple phase logic
    }
}
