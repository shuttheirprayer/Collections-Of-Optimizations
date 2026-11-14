package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class ElysiumBiomeReplacerHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "net.jadenxgamer.elysium_api.impl.mixin.biome.MultiNoiseBiomeSourceMixin";

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName)) {
            return false;
        }
        LOGGER.info("Replacing {} with a biome replacer hook that resolves each biome once", mixinClassName);
        return true;
    }
}
