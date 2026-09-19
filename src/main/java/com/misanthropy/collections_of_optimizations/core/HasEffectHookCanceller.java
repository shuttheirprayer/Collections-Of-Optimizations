package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

public final class HasEffectHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final Set<String> CANCELLED = Set.of(
            "com.Polarice3.Goety.mixin.LivingEntityMixin",
            "auviotre.enigmatic.delicacy.mixin.MixinLivingEntity");

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.contains(mixinClassName)) {
            return false;
        }
        LOGGER.info("Replacing {} with an allocation free hasEffect hook", mixinClassName);
        return true;
    }
}
