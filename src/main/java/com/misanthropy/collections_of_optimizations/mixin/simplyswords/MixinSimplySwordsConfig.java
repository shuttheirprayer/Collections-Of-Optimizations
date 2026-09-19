package com.misanthropy.collections_of_optimizations.mixin.simplyswords;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SimplySwordsConfigCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.sweenus.simplyswords.config.Config", remap = false)
public abstract class MixinSimplySwordsConfig {

    @Inject(
            method = "safeValueFetch(Ljava/lang/String;Ljava/lang/String;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$throttleConfigReread(String type, String category, CallbackInfo ci) {
        int interval = CoOConfig.simplyswordsConfigRereadInterval;
        if (interval <= 0 || type == null || category == null) {
            return;
        }
        if (SimplySwordsConfigCache.stillFresh(type, category, interval)) {
            ci.cancel();
        }
    }
}
