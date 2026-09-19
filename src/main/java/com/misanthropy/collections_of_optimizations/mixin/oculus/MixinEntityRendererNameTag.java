package com.misanthropy.collections_of_optimizations.mixin.oculus;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IrisState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRendererNameTag {

    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipNameTagsInShadowPass(CallbackInfo ci) {
        if (CoOConfig.oculusSkipNameTagsInShadowPass && IrisState.shadowPass()) {
            ci.cancel();
        }
    }
}
