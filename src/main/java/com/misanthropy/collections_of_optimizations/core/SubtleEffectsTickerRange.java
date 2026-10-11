package com.misanthropy.collections_of_optimizations.core;

import einstein.subtle_effects.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class SubtleEffectsTickerRange {

    private static Entity outer;
    private static boolean inner;

    private SubtleEffectsTickerRange() {
    }

    public static boolean inRange(Entity entity, int range) {
        if (range == 128 && entity == outer) {
            outer = null;
            return inner;
        }
        outer = null;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        if (player.is(entity)) {
            if (range == 144) {
                outer = entity;
                inner = true;
            }
            return true;
        }
        Vec3 position = entity.position();
        if (!Util.isChunkLoaded(entity.level(), position.x(), position.z())) {
            return false;
        }
        double distance = player.position().distanceToSqr(position.x(), position.y(), position.z());
        if (range == 144) {
            outer = entity;
            inner = distance < 128.0 * 128.0;
        }
        return distance < (double) range * (double) range;
    }
}
