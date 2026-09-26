package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;

public final class SliceAndDiceWetAir {

    private static volatile boolean sprinklerSeen;

    private SliceAndDiceWetAir() {
    }

    public static void markSprinkler() {
        if (!sprinklerSeen) {
            sprinklerSeen = true;
        }
    }

    public static boolean skipRainCheck() {
        return CoOConfig.sliceanddiceSkipWetAirWithoutSprinklers && !sprinklerSeen;
    }

    public static boolean skipExtinguishCheck(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (CoOConfig.sliceanddiceSkipWetAirWithoutSprinklers && !sprinklerSeen) {
            return true;
        }
        if (entity.level().isClientSide) {
            return CoOConfig.sliceanddiceSkipClientWetAirExtinguish;
        }
        return CoOConfig.sliceanddiceWetAirExtinguishOnlyBurning && entity.getRemainingFireTicks() <= 0;
    }
}
