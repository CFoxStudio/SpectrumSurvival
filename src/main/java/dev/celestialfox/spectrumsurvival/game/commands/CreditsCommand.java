package dev.celestialfox.spectrumsurvival.game.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreditsCommand extends Command {
    public CreditsCommand() {
        super("credits");
        addSyntax(((sender, context) -> {
            if (sender instanceof Player player) {
                player.sendMessage(generateCredits());
            }
        }));
    }

    private static Component generateCredits() {
        ComponentBuilder<TextComponent, TextComponent.Builder> messageBuilder = Component.text().content("\n§lCredits\n").color(NamedTextColor.GOLD);

        messageBuilder.append(Component.text("- §lDeveloper: §eAndus\n", NamedTextColor.YELLOW));
        messageBuilder.append(Component.text("- §lIdeas: §7" + randomizeNames(List.of("Andus", "LucePric", "xnlx0000")) + "\n", NamedTextColor.GRAY));
        messageBuilder.append(Component.text("- §lMaps: §e" + randomizeNames(List.of("LucePric", "Bali__0", "GummyW0rm", "xnlx0000")) + "\n", NamedTextColor.YELLOW));

        return messageBuilder.build();
    }

    private static String randomizeNames(List<String> names) {
        List<String> shuffledNames = new ArrayList<>(names);
        Collections.shuffle(shuffledNames);
        return String.join(", ", shuffledNames);
    }
}
