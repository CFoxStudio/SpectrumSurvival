package dev.celestialfox.spectrumsurvival.game.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class StatsCommand extends Command {
    public StatsCommand() {
        super("stats", "statistics");
        addSyntax(((sender, context) -> {
            final Player player = context.get("player");
            if (sender instanceof Player playerSender) {
                playerSender.sendMessage(Component.text("Stats are not available in this version yet.", NamedTextColor.RED));
            }
        }), ArgumentType.Entity("player"));
    }
}
