package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class FtDotTickHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "net.kayn.fallen_traits.mixin.minecraft.MobEffectInstanceMixin";

    private static final String REPLACEMENT = "com.misanthropy.collections_of_optimizations.mixin.fallentraits.MixinMobEffectInstanceDotTick";

    private static Boolean replacementApplies;

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName)) {
            return false;
        }

        if (replacementApplies == null) {
            replacementApplies = new CoOMixinPlugin().shouldApplyMixin("net.minecraft.world.effect.MobEffectInstance", REPLACEMENT);
            if (replacementApplies) {
                LOGGER.info("Replacing {} with fallentraits.MixinMobEffectInstanceDotTick", CANCELLED);
            }
        }

        return replacementApplies;
    }
}
