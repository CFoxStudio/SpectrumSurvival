package dev.celestialfox.spectrumsurvival.game.commands;

import dev.celestialfox.spectrumsurvival.Server;
import dev.celestialfox.spectrumsurvival.utils.config.StatsSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class StatsCommand extends Command {
    public StatsCommand() {
        super("stats", "statistics");
        if (StatsSettings.getEnabled()) {
            // # /stats <player> #
            addSyntax(((sender, context) -> {
                final Player player = context.get("player");
                if (sender instanceof Player playerSender) {
                    playerSender.sendMessage(Component.text(player + " won: " + Server.stats.getWins(playerSender)
                            + " and lost: " + Server.stats.getLosses(playerSender), NamedTextColor.GOLD));
                }
            }), ArgumentType.Entity("player"));

            // # /stats #
            addSyntax(((sender, context) -> {
                if (sender instanceof Player playerSender) {
                    playerSender.sendMessage(Component.text("You won: " + Server.stats.getWins(playerSender)
                            + " and lost: " + Server.stats.getLosses(playerSender), NamedTextColor.GOLD));
                }
            }));
        }
    }
}
