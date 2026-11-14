package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.OutlineOverlaySkip;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRendererOutlineSkip {

    @Inject(method = "doEntityOutline", at = @At("HEAD"))
    private void coo$beginOutlineOverlay(CallbackInfo ci) {
        OutlineOverlaySkip.beginOverlay();
    }

    @Inject(method = "doEntityOutline", at = @At("RETURN"))
    private void coo$endOutlineOverlay(CallbackInfo ci) {
        OutlineOverlaySkip.endOverlay();
    }
}
