package dev.celestialfox.spectrumsurvival.game.commands;

import dev.celestialfox.spectrumsurvival.game.managers.GameManager;
import dev.celestialfox.spectrumsurvival.game.managers.QueueManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class AboutCommand extends Command {
    public AboutCommand() {
        super("about");
        addSyntax(((sender, context) -> {
            final String arg1 = context.get("arg1");
            if (sender instanceof Player player) {
                if (arg1.equals("us")) {
                    player.sendMessage(
                            Component.text("\n§lAbout CelestialFox Studio\n", NamedTextColor.GOLD)
                                    .append(Component.text("We are a passionate indie game studio creating unique experiences.\n", NamedTextColor.YELLOW))
                                    .append(Component.text("Want to know more? Check our website: ", NamedTextColor.YELLOW))
                                    .append(Component.text("https://celestial-fox.com/", NamedTextColor.LIGHT_PURPLE, TextDecoration.UNDERLINED)
                                            .clickEvent(ClickEvent.openUrl("https://celestial-fox.com/")))
                    );
                } else if (arg1.equals("game")) {
                    player.sendMessage(
                            Component.text("\n§lAbout Spectrum Survival\n", NamedTextColor.DARK_AQUA)
                                    .append(Component.text("Dive into Spectrum Survival, where each color phase brings a new challenge! Every 20 seconds, the color changes, creating a chaotic 3-minute survival test.\n\n", NamedTextColor.AQUA))
                                    .append(Component.text("§lColor Phases:\n", NamedTextColor.LIGHT_PURPLE))
                                    .append(Component.text("- §lRed: §cFlames spread across the map. Avoid getting burned!\n", NamedTextColor.RED))
                                    .append(Component.text("- §lBlue: §9Snowball fight frenzy! Eliminate your foes.\n", NamedTextColor.BLUE))
                                    .append(Component.text("- §lGreen: §aWatch out for wither roses and zombies. Roses hurt, zombies eliminate instantly.\n", NamedTextColor.GREEN))
                                    .append(Component.text("- §lYellow: §eDodge lightning or be struck! §8§o(You can punch players into the lightning)\n", NamedTextColor.YELLOW))
                                    .append(Component.text("- §lGray: §7Darkness falls, pvp enabled. Fight to survive!\n", NamedTextColor.GRAY))
                                    .append(Component.text("\nSurvive all phases and adapt to win! Good luck!", NamedTextColor.AQUA))
                    );
                }
            }
        }), ArgumentType.String("arg1"));
        addSyntax(((sender, context) -> {
            if (sender instanceof Player player) {
                player.sendMessage(Component.text("Use §e/about us §ror §e/about game", NamedTextColor.GRAY));
            }
        }));
    }
}
