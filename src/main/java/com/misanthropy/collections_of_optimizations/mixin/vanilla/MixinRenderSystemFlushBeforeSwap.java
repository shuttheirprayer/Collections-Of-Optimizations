package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.FlushBeforeSwap;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public abstract class MixinRenderSystemFlushBeforeSwap {

    @Inject(method = "flipFrame", at = @At("HEAD"))
    private static void coo$flushBeforeSwap(long window, CallbackInfo ci) {
        FlushBeforeSwap.swap();
    }
}
