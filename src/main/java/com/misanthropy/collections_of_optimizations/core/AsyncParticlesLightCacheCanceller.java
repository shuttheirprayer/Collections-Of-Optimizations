package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class AsyncParticlesLightCacheCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "forge.fun.qu_an.minecraft.asyncparticles.client.mixin.core.particle.light_cache.MixinParticle_LightCache_Late";

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName)) {
            return false;
        }
        LOGGER.info("Replacing {} with asyncparticles.MixinParticleLeanLightCache", mixinClassName);
        return true;
    }
}
