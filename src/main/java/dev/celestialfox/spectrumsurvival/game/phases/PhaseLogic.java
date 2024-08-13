package dev.celestialfox.spectrumsurvival.game.phases;

import dev.celestialfox.spectrumsurvival.game.classes.ZombieCreature;
import dev.celestialfox.spectrumsurvival.utils.Misc;
import dev.celestialfox.spectrumsurvival.game.classes.GameLobby;
import dev.celestialfox.spectrumsurvival.game.managers.ConversionManager;
import net.hollowcube.polar.PolarLoader;
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

    public static void random(GameLobby game) {
        if (repeatTask != null) {
            repeatTask.cancel();
        }

        repeatTask = scheduler.buildTask(() -> {
            // Check if all players are eliminated
            if (game.getEliminated().size() == game.getPlayers().size()) {
                ConversionManager.fromGame(game);
                repeatTask.cancel();
                endTask.cancel();
            } else {
                logger.debug("Starting random phase selection for GameLobby: {}", game.getName());
                try {
                    Phase[] phases = Phase.values();
                    Phase selectedPhase;
                    do {
                        int randomIndex = new Random().nextInt(phases.length);
                        selectedPhase = phases[randomIndex];
                    } while (
                            (selectedPhase == game.getPhase())
                                    || ((selectedPhase == Phase.GRAY) && (game.getPlayers().size() == game.getEliminated().size()+1)));
                    game.setPhase(selectedPhase);

                    logger.debug("Selected phase: {} for GameLobby: {}", selectedPhase.name(), game.getName());
                    if (game.getTask() != null) {
                        game.getTask().cancel();
                    }

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
                logger.debug("Ending game for GameLobby: {}", game.getName());
                ConversionManager.fromGame(game);
                repeatTask.cancel();
            } catch (Exception e) {
                logger.error("Exception occurred during game ending: {}", e.getMessage());
            }
        }).delay(TaskSchedule.seconds(gameTime)).schedule();
    }

    public static void red(GameLobby game) {
        setupPhase(game, Phase.RED, "New color picked! §cRED",
                "Dodge the fire! It does damage to you.",
                "Red", NamedTextColor.RED, 13000, Weather.CLEAR);

        // Logic
        List<Pos> positions = new ArrayList<>();
        int minX = -10;
        int maxX = 10;
        int minZ = -10;
        int maxZ = 10;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = 55; y < 75; y++) {
                    Pos pos = new Pos(x, y, z);
                    if (new Random().nextInt(10) < 2) {
                        if (isFlamableBlock(game.getInstance().getBlock(pos))) {
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
        }).repeat(Duration.ofMillis(250)).schedule();

        scheduler.buildTask(() -> {
            task.cancel();
            resetInstance(game);
        }).delay(TaskSchedule.seconds(phaseDuration-1)).schedule();
    }
    public static void blue(GameLobby game) {
        setupPhase(game, Phase.BLUE, "New color picked! §9BLUE",
                "Battle it out with snowballs! Knock back and disorient your opponents.",
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
                }
            }
        }

        // Snowball Supply
        game.getPlayers().forEach(uuid -> {
            Player player = Misc.getPlayer(uuid);
            player.getInventory().addItemStack(ItemStack.of(Material.SNOWBALL, 64));
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
                "Avoid wither roses and zombies! Wither roses deal damage, while zombies eliminate players instantly.",
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
        scheduler.buildTask(() -> {
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
                    Pos lightningPos = playerPos.add(random.nextInt(11) - 5, 0, random.nextInt(11) - 5
                    );

                    Entity lightning = new Entity(EntityType.LIGHTNING_BOLT);
                    lightning.setInstance(randomPlayer.getInstance(), lightningPos);
                    lightning.spawn();
                }
            }).repeat(Duration.ofSeconds(5)).schedule();
            game.setTask(task);
        }).delay(Duration.ofSeconds(5)).schedule();
    }
    public static void gray(GameLobby game) {
        setupPhase(game, Phase.GRAY, "New color picked! §8GRAY",
                "Darkness everywhere, PvP active. Fight everyone to survive.",
                "Gray", NamedTextColor.DARK_GRAY, 18000, Weather.CLEAR);

        // Logic
        game.getPlayers().forEach(uuid -> {
            if (Misc.getPlayer(uuid).getGameMode() == GameMode.ADVENTURE) {
                Misc.getPlayer(uuid).addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, phaseDuration*20));
                Misc.getPlayer(uuid).addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, phaseDuration*20));
            }
        });
        scheduler.buildTask(() -> {
            startHealthRegeneration(game);
        }).delay(TaskSchedule.seconds(phaseDuration)).schedule();
    }


    public static void setupPhase(GameLobby game, Phase phase, String colorPicked, String desc,
                                  String colorText, NamedTextColor color, int time, Weather weather) {
        game.setPhase(phase);
        game.sendMessage(Component.text(colorPicked, NamedTextColor.GRAY));
        game.sendMessage(Component.text(desc, color));
        Misc.showTitle(game.getInstance(), Component.text("■", color), Component.text(colorText, color));

        long currentGameTime = game.getInstance().getTime();
        if (time != currentGameTime) {
            Misc.transitionToTime(game, Duration.ofSeconds(2), currentGameTime, time);
        }

        game.getInstance().setWeather(weather);
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

        startHealthRegeneration(game);
    }

    private static boolean isFlamableBlock(Block block) {
        return !block.compare(Block.AIR)
                && !block.compare(Block.GRASS_BLOCK)
                && !block.compare(Block.FARMLAND)
                && !block.compare(Block.STONE)
                && !block.compare(Block.ANDESITE)
                && !block.compare(Block.ANDESITE_STAIRS)
                && !block.compare(Block.ANDESITE_SLAB)
                && !block.compare(Block.GRAVEL)
                && !block.compare(Block.DIRT_PATH)
                && !block.compare(Block.OAK_FENCE)
                && !block.compare(Block.OAK_FENCE_GATE)
                && !block.compare(Block.STONE_BRICKS)
                && !block.compare(Block.MOSSY_STONE_BRICK_WALL)
                && !block.compare(Block.MOSSY_STONE_BRICKS)
                && !block.compare(Block.MOSSY_STONE_BRICK_STAIRS)
                && !block.compare(Block.STONE_BRICK_STAIRS)
                && !block.compare(Block.MOSSY_STONE_BRICK_SLAB)
                && !block.compare(Block.STONE_BRICK_WALL)
                && !block.compare(Block.BRICK_SLAB)
                && !block.compare(Block.BRICK_STAIRS)
                && !block.compare(Block.CHAIN)
                && !block.compare(Block.WATER_CAULDRON)
                && !block.compare(Block.OAK_TRAPDOOR)
                && !block.compare(Block.SPRUCE_FENCE)
                && !block.compare(Block.SPRUCE_FENCE_GATE)
                && !block.compare(Block.SPRUCE_TRAPDOOR)
                && !block.compare(Block.SPRUCE_WALL_SIGN)
                && !block.compare(Block.SPRUCE_STAIRS)
                && !block.compare(Block.BARRIER)
                && !block.compare(Block.SAND)
                && !block.compare(Block.MOSS_BLOCK)
                && !block.compare(Block.WATER)
                && !block.compare(Block.SEA_LANTERN)
                && !block.compare(Block.RED_CARPET)
                && !block.compare(Block.WHITE_CARPET)
                && !block.compare(Block.TRIPWIRE);
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

    public static void startHealthRegeneration(GameLobby game) {
        int regenerationInterval = 20; // in seconds

        Task healthRegenTask = scheduler.buildTask(() -> {
            for (UUID playerId : game.getPlayers()) {
                Player player = Misc.getPlayer(playerId);
                if (player != null && player.getGameMode() == GameMode.ADVENTURE) {
                    player.addEffect(new Potion(PotionEffect.REGENERATION, (byte) 1, 100));
                }
            }
        }).repeat(TaskSchedule.seconds(regenerationInterval)).schedule();

        scheduler.buildTask(healthRegenTask::cancel).delay(TaskSchedule.seconds(phaseDuration)).schedule();
    }
}
