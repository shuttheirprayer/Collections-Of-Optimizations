package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;

public final class CableFacadeGuard {

    private CableFacadeGuard() {
    }

    public static Object get(ThreadLocal<?> guard, BlockGetter getter) {
        if (CoOConfig.cablefacadesSkipGuardWhenEmpty && getter != null && !(getter instanceof ServerLevel) && !CableFacadePresence.any()) {
            return Boolean.TRUE;
        }
        return guard.get();
    }
}
