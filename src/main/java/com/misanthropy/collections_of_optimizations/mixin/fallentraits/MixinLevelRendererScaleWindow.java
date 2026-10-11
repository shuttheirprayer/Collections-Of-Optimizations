package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtSizeScaleCache;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRendererScaleWindow {

    @Inject(method = "renderLevel", at = @At("HEAD"), require = 0)
    private void coo$openScaleWindow(CallbackInfo ci) {
        FtSizeScaleCache.rendering = true;
    }

    @Inject(method = "renderLevel", at = @At("RETURN"), require = 0)
    private void coo$closeScaleWindow(CallbackInfo ci) {
        FtSizeScaleCache.rendering = false;
    }
}
