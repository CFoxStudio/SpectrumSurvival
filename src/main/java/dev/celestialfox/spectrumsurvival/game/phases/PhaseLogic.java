package dev.celestialfox.spectrumsurvival.game.phases;

import dev.celestialfox.spectrumsurvival.game.classes.ZombieCreature;
import dev.celestialfox.spectrumsurvival.game.managers.GameManager;
import dev.celestialfox.spectrumsurvival.utils.Misc;
import dev.celestialfox.spectrumsurvival.game.classes.GameLobby;
import dev.celestialfox.spectrumsurvival.game.managers.ConversionManager;
import net.hollowcube.polar.PolarLoader;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.Weather;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

public class PhaseLogic {
    private static final Logger logger = LoggerFactory.getLogger(PhaseLogic.class);
    private static final SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
    private static final int phaseDuration = 20; // in seconds
    private static final int gameTime = 180; // 3 minutes in seconds
    private static Task repeatTask;
    private static Task endTask;

    private static final int zombiesSpawn = 5;
    private static BossBar nextPhaseBossBar;

    public static void random(GameLobby game) {
        if (game.getRepeatTask() != null) {
            game.getRepeatTask().cancel();
        }
        if (game.getEndTask() != null) {
            game.getEndTask().cancel();
        }

        nextPhaseBossBar = BossBar.bossBar(
                Component.text("Time until next phase", NamedTextColor.WHITE),
                1.0f, // progress (between 0.0 and 1.0)
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );

        game.getPlayers().forEach(uuid -> Misc.getPlayer(uuid).showBossBar(nextPhaseBossBar));

        Task bossBarTask = scheduler.buildTask(() -> PhaseBossBar.updateBossBar(game, nextPhaseBossBar, phaseDuration))
                .repeat(TaskSchedule.tick(1))
                .schedule();

        game.setBossBarTask(bossBarTask);

        repeatTask = scheduler.buildTask(() -> {
            // Check if all players are eliminated
            if (game.getEliminated().size() == game.getPlayers().size()) {
                endGame(game);
                game.getEndTask().cancel();
            } else {
                logger.debug("Starting random phase selection for GameLobby: {}", game.getName());
                if (game.getTask() != null) {
                    game.getTask().cancel();
                }
                try {
                    Phase[] phases = Phase.values();
                    Phase selectedPhase;
                    Random random = new Random(System.nanoTime());
                    do {
                        selectedPhase = phases[random.nextInt(phases.length)];
                    } while (
                            (selectedPhase == game.getPhase())
                                    || ((selectedPhase == Phase.GRAY || selectedPhase == Phase.BLUE) && (game.getPlayers().size() == game.getEliminated().size()+1)));
                    game.setPhase(selectedPhase);

                    logger.debug("Selected phase: {} for GameLobby: {}", selectedPhase.name(), game.getName());
                    switch (selectedPhase) {
                        case RED -> red(game);
                        case BLUE -> blue(game);
                        case GREEN -> green(game);
                        case YELLOW -> yellow(game);
                        case GRAY -> gray(game);
                    }
                } catch (Exception e) {
                    logger.error("Exception occurred during phase execution: {}", e.getMessage());
                }
            }
        }).repeat(TaskSchedule.seconds(phaseDuration)).schedule();

        endTask = scheduler.buildTask(() -> {
            try {
                endGame(game);
            } catch (Exception e) {
                logger.error("Exception occurred during game ending: {}", e.getMessage());
            }
        }).delay(TaskSchedule.seconds(gameTime)).schedule();

        game.setPhaseStartTime(System.currentTimeMillis());
        game.setRepeatTask(repeatTask);
        game.setEndTask(endTask);
    }

    public static void red(GameLobby game) {
        setupPhase(game, Phase.RED, "New color picked! §cRED",
                "Dodge the fire! It does damage to you.",
                "Red", NamedTextColor.RED, 13000, Weather.CLEAR);

        // Logic
        List<Pos> positions = new ArrayList<>();
        int minX = -20;
        int maxX = 20;
        int minZ = -20;
        int maxZ = 20;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = 66; y < 70; y++) {
                    Pos pos = new Pos(x, y, z);
                    if (new Random().nextInt(5) < 2) {
                        if (Misc.isFlamableBlock(game.getInstance().getBlock(pos))) {
                            positions.add(pos);
                        }
                    }
                }
            }
        }

        Iterator<Pos> iterator = positions.iterator();
        Task task;
        task = scheduler.buildTask(() -> {
            if (iterator.hasNext()) {
                Pos pos = iterator.next();
                game.getInstance().setBlock(pos, Block.FIRE);
            }
        }).repeat(TaskSchedule.tick(1)).schedule();

        scheduler.buildTask(() -> {
            task.cancel();
            resetInstance(game);
        }).delay(TaskSchedule.seconds(phaseDuration-1)).schedule();
    }
    public static void blue(GameLobby game) {
        setupPhase(game, Phase.BLUE, "New color picked! §9BLUE",
                "Battle it out with snowballs! Eliminate everyone.",
                "Blue", NamedTextColor.BLUE, 1000, Weather.CLEAR);

        // Logic
        int minX = -20;
        int maxX = 20;
        int minZ = -20;
        int maxZ = 20;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = 55; y < 75; y++) {
                    Pos pos = new Pos(x, y, z);
                    Block block = game.getInstance().getBlock(pos);
                    if (block.compare(Block.GRASS_BLOCK) || block.compare(Block.SAND) || block.compare(Block.MOSS_BLOCK)) {
                        game.getInstance().setBlock(pos, Block.SNOW_BLOCK);
                    }
                    if (block.compare(Block.MOSS_CARPET) || block.compare(Block.SHORT_GRASS) || block.compare(Block.TALL_GRASS)) {
                        game.getInstance().setBlock(pos, Block.AIR);
                    }
                    if (block.compare(Block.HAY_BLOCK)) {
                        game.getInstance().setBlock(pos, Block.DRIED_KELP_BLOCK);
                    }
                }
            }
        }

        game.getInstance().setBlock(new Pos(5, 66, 3), Block.SNOW_BLOCK);
        game.getInstance().setBlock(new Pos(5, 67, 3), Block.SNOW_BLOCK);
        game.getInstance().setBlock(new Pos(5, 68, 3), Block.CARVED_PUMPKIN);

        game.getInstance().setBlock(new Pos(-10, 66, -1), Block.SNOW_BLOCK);
        game.getInstance().setBlock(new Pos(-10, 67, -1), Block.SNOW_BLOCK);

        // Snowball Supply
        game.getPlayers().forEach(uuid -> {
            Player player = Misc.getPlayer(uuid);
            player.getInventory().addItemStack(ItemStack.of(Material.SNOWBALL, 16));
        });

        // Phase end
        scheduler.buildTask(() -> {
            game.getPlayers().forEach(uuid -> {
                Player player = Misc.getPlayer(uuid);
                player.getInventory().clear();
            });
            resetInstance(game);
        }).delay(TaskSchedule.seconds(phaseDuration-1)).schedule();
    }
    public static void green(GameLobby game) {
        setupPhase(game, Phase.GREEN, "New color picked! §aGREEN",
                "Wither roses harm, zombies kill instantly.",
                "Green", NamedTextColor.GREEN, 18000, Weather.CLEAR);

        // Logic
        scheduler.buildTask(() -> spawnZombies(game)).delay(TaskSchedule.seconds(zombiesSpawn)).schedule();

        int minX = -20;
        int maxX = 20;
        int minZ = -20;
        int maxZ = 20;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = 55; y < 75; y++) {
                    Pos pos = new Pos(x, y, z);
                    Block block = game.getInstance().getBlock(pos);
                    if (block.compare(Block.SHORT_GRASS)) {
                        game.getInstance().setBlock(pos, Block.WITHER_ROSE);
                    }
                }
            }
        }
    }
    public static void yellow(GameLobby game) {
        setupPhase(game, Phase.YELLOW, "New color picked! §eYELLOW",
                "Thunder is coming! Find a place to hide!",
                "Yellow", NamedTextColor.YELLOW, 18000, Weather.RAIN);

        // Logic
        Task task = scheduler.buildTask(() -> {
            List<UUID> uuids = game.getPlayers();
            List<Player> eligiblePlayers = new ArrayList<>();

            for (UUID uuid : uuids) {
                Player player = Misc.getPlayer(uuid);
                if (player.getGameMode() == GameMode.ADVENTURE && !isUnderBlock(player)) {
                    eligiblePlayers.add(player);
                }
            }

            if (!eligiblePlayers.isEmpty()) {
                Random random = new Random();
                Player randomPlayer = eligiblePlayers.get(random.nextInt(eligiblePlayers.size()));
                Entity lightning = new Entity(EntityType.LIGHTNING_BOLT);
                lightning.setInstance(randomPlayer.getInstance(), randomPlayer.getPosition());
                lightning.spawn();
                game.eliminate(randomPlayer);
            } else {
                Random random = new Random();
                Player randomPlayer = Misc.getPlayer(uuids.get(random.nextInt(uuids.size())));
                Pos playerPos = randomPlayer.getPosition();
                Pos lightningPos = playerPos.add(random.nextInt(11) - 5, 0, random.nextInt(11) - 5);

                Entity lightning = new Entity(EntityType.LIGHTNING_BOLT);
                lightning.setInstance(randomPlayer.getInstance(), lightningPos);
                lightning.spawn();
            }
        }).delay(Duration.ofSeconds(5)).repeat(Duration.ofSeconds(3)).schedule();
        game.setTask(task);
    }
    public static void gray(GameLobby game) {
        setupPhase(game, Phase.GRAY, "New color picked! §8GRAY",
                "Darkness everywhere, PvP active. Fight everyone to survive.",
                "Gray", NamedTextColor.GRAY, 18000, Weather.CLEAR);

        // Logic
        game.getPlayers().forEach(uuid -> {
            if (Misc.getPlayer(uuid).getGameMode() == GameMode.ADVENTURE) {
                Misc.getPlayer(uuid).addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, phaseDuration*20));
                Misc.getPlayer(uuid).addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, phaseDuration*20));
            }
        });
    }


    public static void setupPhase(GameLobby game, Phase phase, String colorPicked, String desc,
                                  String colorText, NamedTextColor color, int time, Weather weather) {
        PhaseBossBar.resetBossBarProgress(nextPhaseBossBar);
        game.setPhaseStartTime(System.currentTimeMillis());
        PhaseBossBar.updateBossBar(game, nextPhaseBossBar, phaseDuration);

        game.setPhase(phase);
        game.sendMessage(Component.text(colorPicked, NamedTextColor.WHITE));
        game.sendMessage(Component.text(desc, color));
        Misc.showTitle(game.getInstance(), Component.text("■ " + colorText + " ■", color), Component.text(desc, color));

        long currentGameTime = game.getInstance().getTime();
        if (time != currentGameTime) {
            Misc.transitionToTime(game, Duration.ofSeconds(2), currentGameTime, time);
        }

        game.getInstance().setWeather(weather);
    }

    public static void endGame(GameLobby game) {
        logger.debug("Ending game for GameLobby: {}", game.getName());
        ConversionManager.fromGame(game);
        game.getRepeatTask().cancel();
        game.getBossBarTask().cancel();
        if (game.getTask() != null) {
            game.getTask().cancel();
        }
        PhaseBossBar.removeBossBar(game, nextPhaseBossBar);
    }

    private static boolean isUnderBlock(Player player) {
        Pos playerPosition = player.getPosition();
        for (int i = 1; i <= 5; i++) {
            Pos posAbove = playerPosition.add(0, i, 0);
            Block blockAbove = player.getInstance().getBlock(posAbove);
            if (blockAbove.isSolid()) {
                return true;
            }
        }

        return false;
    }

    private static void spawnZombies(GameLobby game) {
        List<Pos> spawnLocations = Arrays.asList(
                new Pos(8, 65, 15),
                new Pos(-15, 67, 7),
                new Pos(11, 66, -12),
                new Pos(18, 64, 3)
        );
        Collections.shuffle(spawnLocations);

        int numZombiesToSpawn = Math.min(3, spawnLocations.size());
        for (int i = 0; i < numZombiesToSpawn; i++) {
            Pos pos = spawnLocations.get(i);
            Entity zombie = new ZombieCreature();
            zombie.setInstance(game.getInstance(), pos);
            zombie.spawn();
        }

        scheduler.buildTask(() -> endGreen(game))
                .delay(TaskSchedule.seconds(phaseDuration - zombiesSpawn))
                .schedule();
    }

    private static void endGreen(GameLobby game) {
        for (Entity entity : game.getInstance().getEntities()) {
            if (entity.getEntityType() == EntityType.ZOMBIE) {
                entity.remove();
            }
        }

        int minX = -100;
        int maxX = 100;
        int minZ = -100;
        int maxZ = 100;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = 55; y < 75; y++) {
                    Pos pos = new Pos(x, y, z);
                    Block block = game.getInstance().getBlock(pos);
                    if (block.compare(Block.WITHER_ROSE)) {
                        game.getInstance().setBlock(pos, Block.SHORT_GRASS);
                    }
                }
            }
        }
    }

    public static void resetInstance(GameLobby game) {
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        try {
            instanceContainer.setChunkLoader(new PolarLoader(Path.of("worlds/game.polar")));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        game.setInstance(instanceContainer);
    }


}
