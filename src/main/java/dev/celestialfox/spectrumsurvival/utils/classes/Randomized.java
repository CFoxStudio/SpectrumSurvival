package dev.celestialfox.spectrumsurvival.utils.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

import java.util.Random;

public class Randomized {
    public static Component elimination(Player player) {
        Random rand = new Random();
        int message = rand.nextInt(4);

        String elIcon = "§7[§r☠§7] §b§l";
        return switch (message) {
            case 0 -> Component.text(elIcon + player.getUsername() + " §cshould try Roblox.");
            case 1 -> Component.text(elIcon + player.getUsername() + " §cdidn't try good enough.");
            case 2 -> Component.text(elIcon + player.getUsername() + " §cded.");
            case 3 -> Component.text(elIcon + player.getUsername() + " §cthought they were God.");
            default -> Component.text(elIcon + player.getUsername() + " §cgot eliminated.");
        };
    }

    public static NamedTextColor madeByColor() {
        Random random = new Random();
        int num = random.nextInt(4);
        switch (num) {
            case 0 -> {
                return NamedTextColor.BLUE;
            }
            case 1 -> {
                return NamedTextColor.GRAY;
            }
            case 2 -> {
                return NamedTextColor.GOLD;
            }
            case 3 -> {
                return NamedTextColor.GREEN;
            }
            default -> {
                return NamedTextColor.RED;
            }
        }
    }
}
