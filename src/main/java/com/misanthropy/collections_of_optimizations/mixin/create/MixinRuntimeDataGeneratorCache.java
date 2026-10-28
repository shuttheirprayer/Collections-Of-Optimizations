package com.misanthropy.collections_of_optimizations.mixin.create;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CreateRuntimeDataCache;
import com.simibubi.create.foundation.data.RuntimeDataGenerator;
import com.simibubi.create.foundation.pack.DynamicPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RuntimeDataGenerator.class, remap = false)
public abstract class MixinRuntimeDataGeneratorCache {

    @Inject(method = "insertIntoPack", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$fromCache(DynamicPack dynamicPack, CallbackInfo ci) {
        if (!CoOConfig.createCacheRuntimeData) {
            return;
        }
        CreateRuntimeDataCache.get().forEach(dynamicPack::put);
        ci.cancel();
    }
}
