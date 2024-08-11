package dev.cfox.gamejam.game.commands;

import dev.cfox.gamejam.game.managers.QueueManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

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
                }
            }
        }), ArgumentType.String("arg1"));
    }
}
