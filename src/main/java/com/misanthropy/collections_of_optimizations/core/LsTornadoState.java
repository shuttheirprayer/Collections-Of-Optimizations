package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.Entity;

public final class LsTornadoState {

    private static volatile long lastTornadoTick = -1000L;

    private LsTornadoState() {
    }

    public static void markTornadoTick(Entity tornado) {
        lastTornadoTick = tornado.level().getGameTime();
    }

    public static boolean tornadoTickedRecently(Entity entity) {
        return entity.level().getGameTime() - lastTornadoTick <= 1L;
    }
}
