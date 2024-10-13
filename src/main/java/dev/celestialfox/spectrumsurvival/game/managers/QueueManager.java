package dev.celestialfox.spectrumsurvival.game.managers;

import dev.celestialfox.spectrumsurvival.utils.Misc;
import dev.celestialfox.spectrumsurvival.game.classes.GameQueue;
import dev.celestialfox.spectrumsurvival.utils.config.GameSettings;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueueManager {
    public static List<GameQueue> queues = new ArrayList<>();
    private static final HashMap<GameQueue, Task> countdownTasks = new HashMap<>();
    private static int minPlayers = GameSettings.getMinSlots();
    private static int maxPlayers = GameSettings.getMaxSlots();
    private static int waitTime = GameSettings.getWaitTime(); // in seconds
    public static Instance lobbyInstance;

    public static void joinPlayer(Player player) {
        if (!GameManager.isPlayerInGame(player)) {
            boolean queueFound = false;

            if (!queues.isEmpty()) {
                for (GameQueue queue : queues) {
                    if (queue.getPlayers().size() < maxPlayers) {
                        queue.addPlayer(player.getUuid());
                        queueFound = true;
                        if (queue.getPlayers().size() >= minPlayers) {
                            startCountdown(queue);
                        }
                        break;
                    }
                }
            }

            if (!queueFound) {
                createQueue(player);
            }

            player.sendMessage(Component.text("The queue starts the countdown from §e" + minPlayers + " players.", NamedTextColor.GRAY));
            player.sendMessage(Component.text("One queue can hold up to §e" + maxPlayers + " players.", NamedTextColor.GRAY));
            player.sendMessage(Component.text("§e§lWaiting for too long? §rUse §a/queue force §rto start now.", NamedTextColor.GRAY));
        }
    }

    public static void removePlayer(Player player) {
        GameQueue queue = getPlayerQueue(player);
        if (queue != null) {
            queue.removePlayer(player.getUuid());
            if (queue.getPlayers().size() < minPlayers) {
                stopCountdown(queue);
            }
        }
    }

    public static void createQueue(Player player) {
        GameQueue newQueue = new GameQueue();
        newQueue.addPlayer(player.getUuid());
        queues.add(newQueue);
    }

    public static void removeQueue(GameQueue queue) {
        queues.remove(queue);
    }

    public static GameQueue getPlayerQueue(Player player) {
        return queues.stream()
                .filter(queue -> queue.getPlayers().contains(player.getUuid()))
                .findFirst()
                .orElse(null);
    }

    private static void startCountdown(GameQueue queue) {
        // Check if a countdown task is already running for the queue
        if (countdownTasks.containsKey(queue)) {
            return;
        }

        final int[] countdown = {waitTime}; // Countdown timer in seconds

        // Schedule a task to send the title every second
        Task task = MinecraftServer.getSchedulerManager().buildTask(() -> {
            queue.getPlayers().forEach(player -> Misc.getPlayer(player).showTitle(Title.title(
                            Component.text("Starting in " + countdown[0], NamedTextColor.GREEN),
                            Component.text("Get ready", NamedTextColor.GRAY))));
            queue.getPlayers().forEach(player -> {
                Misc.getPlayer(player).playSound(Sound.sound(Key.key("minecraft", "entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 1f));
            });

            if (countdown[0] == 0) {
                // Time's up, cancel the task and start the game
                queue.getPlayers().forEach(player -> {
                    Misc.getPlayer(player).playSound(Sound.sound(Key.key("minecraft", "entity.player.levelup"), Sound.Source.MASTER, 1f, 1f));
                });
                GameManager.startGame(queue);
                countdownTasks.get(queue).cancel();
                countdownTasks.remove(queue);
            }

            countdown[0]--;
        }).repeat(TaskSchedule.seconds(1)).schedule();

        countdownTasks.put(queue, task);
    }

    private static void stopCountdown(GameQueue queue) {
        if (countdownTasks.containsKey(queue)) {
            Task countdownTask = countdownTasks.get(queue);
            countdownTask.cancel();
            countdownTasks.remove(queue);
        }
    }

    public static boolean isPlayerInQueue(Player player) {
        for (GameQueue queue : queues) {
            if (queue.getPlayers().contains(player.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public static int getPlayersInQueue() {
        int totalPlayers = 0;
        for (GameQueue queue : queues) {
            totalPlayers += queue.getPlayers().size();
        }
        return totalPlayers;
    }
}
