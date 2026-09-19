package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class DuplicationlessClientEntities {

    private DuplicationlessClientEntities() {
    }

    public static Iterable<Entity> forRendering(Level level) {
        return ((ClientLevel) level).entitiesForRendering();
    }
}
