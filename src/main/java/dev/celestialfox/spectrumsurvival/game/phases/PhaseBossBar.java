package dev.celestialfox.spectrumsurvival.game.phases;

import dev.celestialfox.spectrumsurvival.game.classes.GameLobby;
import dev.celestialfox.spectrumsurvival.utils.Misc;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PhaseBossBar {
    public static void updateBossBar(GameLobby game, BossBar nextPhaseBossBar, int phaseDuration) {
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - game.getPhaseStartTime();
        long elapsedSeconds = elapsedMillis / 1000;
        float progress = 1.0f - (float) elapsedSeconds / phaseDuration;
        nextPhaseBossBar.progress(Math.max(progress, 0.0f));
        long remainingSeconds = phaseDuration - elapsedSeconds;
        String remainingTime = String.format("%d seconds until next phase", Math.max(remainingSeconds, 0));
        nextPhaseBossBar.name(Component.text(remainingTime, NamedTextColor.WHITE));
    }

    public static void resetBossBarProgress(BossBar nextPhaseBossBar) {
        nextPhaseBossBar.progress(1.0f);
    }

    public static void removeBossBar(GameLobby game, BossBar nextPhaseBossBar) {
        game.getPlayers().forEach(uuid -> Misc.getPlayer(uuid).hideBossBar(nextPhaseBossBar));
    }
}
