package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class AsyncParticlesBlockEntityHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "forge.fun.qu_an.minecraft.asyncparticles.client.mixin.conditional.MixinLevelChunk_SafeBlockEntityMap_Off";

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName)) {
            return false;
        }
        LOGGER.info("Replacing {} with an allocation free getBlockEntity hook", mixinClassName);
        return true;
    }
}
