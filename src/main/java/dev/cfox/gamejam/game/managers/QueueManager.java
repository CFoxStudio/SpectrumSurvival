package dev.cfox.gamejam.game.managers;

import dev.cfox.gamejam.game.classes.GameQueue;
import dev.cfox.gamejam.utils.Misc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueueManager {
    public static List<GameQueue> queues = new ArrayList<>();
    private static final HashMap<GameQueue, Task> countdownTasks = new HashMap<>();

    public static void joinPlayer(Player player) {
        boolean queueFound = false;

        if (!queues.isEmpty()) {
            for (GameQueue queue : queues) {
                if (queue.getPlayers().size() < 10) {
                    queue.addPlayer(player.getUuid());
                    queueFound = true;
                    if (queue.getPlayers().size() >= 6) {
                        startCountdown(queue);
                    }
                    break;
                }
            }
        }

        if (!queueFound) {
            createQueue(player);
            player.sendMessage(Component.text("No queues were found! A new queue has been created just for you.", NamedTextColor.GREEN));
        }
    }

    public static void removePlayer(Player player) {
        GameQueue queue = getPlayerQueue(player);
        if (queue != null) {
            queue.removePlayer(player.getUuid());
            if (queue.getPlayers().size() < 2) {
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

        final int[] countdown = {5}; // Countdown timer in seconds

        // Schedule a task to send the title every second
        Task task = MinecraftServer.getSchedulerManager().buildTask(() -> {
            queue.getPlayers().forEach(player -> Misc.getPlayer(player).showTitle(Title.title(
                            Component.text("Starting in " + countdown[0], NamedTextColor.GREEN),
                            Component.text("Get ready", NamedTextColor.GRAY))));

            if (countdown[0] == 0) {
                // Time's up, cancel the task and start the game
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
}
