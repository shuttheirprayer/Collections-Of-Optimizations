package com.misanthropy.collections_of_optimizations.core;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class DoesPotatoTickRenderHookCanceller implements MixinCanceller {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");

    private static final String CANCELLED = "me.kall.doespotatotick.mixin.client.EntityRenderDispatcherMixin";

    private static boolean announced;

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (!CANCELLED.equals(mixinClassName)) {
            return false;
        }

        if (!announced) {
            announced = true;
            LOGGER.info("Replacing {} with doespotatotick.MixinEntityRenderDispatcherTickable", CANCELLED);
        }

        return true;
    }
}
