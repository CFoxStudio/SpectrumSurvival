package dev.celestialfox.spectrumsurvival.game.classes;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerProjectile;

public class SnowballProjectile extends PlayerProjectile {
    public SnowballProjectile(Entity shooter) {
        super(shooter, EntityType.SNOWBALL);
    }
}
