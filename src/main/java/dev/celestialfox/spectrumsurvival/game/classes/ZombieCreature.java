package dev.celestialfox.spectrumsurvival.game.classes;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.utils.time.TimeUnit;

import java.util.List;

public class ZombieCreature extends EntityCreature {

    public ZombieCreature() {
        super(EntityType.ZOMBIE);
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
        addAIGroup(
                List.of(
                        new MeleeAttackGoal(this, 1.6, 20, TimeUnit.SERVER_TICK), // Attack the target
                        new RandomStrollGoal(this, 20) // Walk around
                ),
                List.of(
                        new ClosestEntityTarget(this, 64, entity -> entity instanceof Player player
                                && player.getGameMode() != GameMode.SPECTATOR) // If there is none, target the nearest player
                )
        );
    }
}