package com.misanthropy.collections_of_optimizations.mixin.oculus;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IrisState;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderBuffers.class, priority = 1500)
public abstract class MixinRenderBuffersBatching {

    @Unique
    private int coo$skipDepth;

    @Inject(method = "beginLevelRendering()V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipBatchingWithoutShaders(CallbackInfo ci) {
        if (this.coo$skipDepth > 0
                || (CoOConfig.oculusSkipBatchingWithoutShaders && !IrisState.shaderPackInUse())) {
            this.coo$skipDepth++;
            ci.cancel();
        }
    }

    @Inject(method = "endLevelRendering()V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$skipUnbatchingWithoutShaders(CallbackInfo ci) {
        if (this.coo$skipDepth > 0) {
            this.coo$skipDepth--;
            ci.cancel();
        }
    }
}
