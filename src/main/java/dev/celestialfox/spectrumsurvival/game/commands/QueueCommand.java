package dev.celestialfox.spectrumsurvival.game.commands;

import dev.celestialfox.spectrumsurvival.game.managers.GameManager;
import dev.celestialfox.spectrumsurvival.game.managers.QueueManager;
import dev.celestialfox.spectrumsurvival.utils.config.GameSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                } else if (arg1.equals("force")) {
                    if (GameSettings.getAllowForce()) {
                        if (QueueManager.getPlayerQueue(player) != null) {
                            GameManager.startGame(QueueManager.getPlayerQueue(player));
                        } else {
                            player.sendMessage(Component.text("You're not in a queue!", NamedTextColor.RED));
                        }
                    } else {
                        player.sendMessage(Component.text("/queue force command is disabled in the config", NamedTextColor.RED));
                    }
                }
            }
        }), ArgumentType.String("arg1"));
    }
}
