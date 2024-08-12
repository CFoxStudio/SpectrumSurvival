package dev.cfox.gamejam.game.commands;

import dev.cfox.gamejam.game.managers.GameManager;
import dev.cfox.gamejam.game.managers.QueueManager;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.NamespaceID;

import java.util.concurrent.TimeUnit;

public class QueueCommand extends Command {
    public QueueCommand() {
        super("queue", "q");
        addSyntax(((sender, context) -> {
            final String arg1 = context.get("arg1");
            if (sender instanceof Player player) {
                if (arg1.equals("join")) {
                    QueueManager.joinPlayer(player);
                } else if (arg1.equals("leave")) {
                    QueueManager.removePlayer(player);
                } else if (arg1.equals("force")) {
                    GameManager.startGame(QueueManager.getPlayerQueue(player));
                }
            }
        }), ArgumentType.String("arg1"));
    }
}
