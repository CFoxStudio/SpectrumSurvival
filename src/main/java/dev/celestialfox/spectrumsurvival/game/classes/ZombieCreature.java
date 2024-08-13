package dev.celestialfox.spectrumsurvival.game.classes;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.utils.time.TimeUnit;

import java.util.List;

public class ZombieCreature extends EntityCreature {

    public ZombieCreature() {
        super(EntityType.ZOMBIE);

        getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.15);

        addAIGroup(
                List.of(
                        new MeleeAttackGoal(this, 1.6, 20, TimeUnit.SERVER_TICK) // Attack the target
                ),
                List.of(
                        new ClosestEntityTarget(this, 50,
                                entity -> entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) // Target the nearest player
                )
        );
    }
}