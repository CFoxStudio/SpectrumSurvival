package dev.celestialfox.spectrumsurvival.game.classes;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.EntityProjectile;

public class SnowballProjectile extends EntityProjectile {
    public SnowballProjectile(Entity shooter) {
        super(shooter, EntityType.SNOWBALL);
    }
}
