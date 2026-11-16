package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RenderTargetLeaks;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderTarget.class)
public abstract class MixinRenderTargetLeakFree {

    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    public int frameBufferId;
    @Shadow
    protected int colorTextureId;
    @Shadow
    protected int depthBufferId;

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        try {
            if (this.frameBufferId > -1 && CoOConfig.vanillaFreeLeakedRenderTargets) {
                RenderTargetLeaks.report(this.getClass().getName(), this.width, this.height, this.frameBufferId, this.colorTextureId, this.depthBufferId);
            }
        } finally {
            super.finalize();
        }
    }
}
