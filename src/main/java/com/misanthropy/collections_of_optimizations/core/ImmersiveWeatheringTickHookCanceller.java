package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class ImmersiveWeatheringTickHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "com.ordana.immersive_weathering.mixins.ServerLevelMixin";
    private static final String REPLACEMENT = "com.misanthropy.collections_of_optimizations.mixin.immersiveweathering.MixinServerLevelGrowthTick";

    private static Boolean replaced;

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName)) {
            return false;
        }
        if (replaced == null) {
            replaced = new CoOMixinPlugin().shouldApplyMixin("net.minecraft.server.level.ServerLevel", REPLACEMENT);
            if (replaced) {
                LOGGER.info("Replacing {} with an allocation free random tick hook", CANCELLED);
            }
        }
        return replaced;
    }
}
