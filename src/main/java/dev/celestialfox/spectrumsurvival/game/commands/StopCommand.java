package dev.celestialfox.spectrumsurvival.game.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class StopCommand  extends Command {
    public StopCommand() {
        super("stop");
        addSyntax(((sender, context) -> {
            if (sender instanceof Player player) {
                player.sendMessage("No.");
            } else {
                MinecraftServer.stopCleanly();
            }
        }));
    }
}
