package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FastBlit;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTarget.class)
public abstract class MixinRenderTargetFastBlit {

    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    public int viewWidth;
    @Shadow
    public int viewHeight;
    @Shadow
    public int frameBufferId;

    @Inject(method = "_blitToScreen", at = @At("HEAD"), cancellable = true)
    private void coo$framebufferBlit(int w, int h, boolean disableBlend, CallbackInfo ci) {
        if (!FastBlit.enabled || !CoOConfig.vanillaFramebufferBlit || w != this.viewWidth || h != this.viewHeight
                || this.width != w || this.height != h || GL11C.glGetInteger(GL30C.GL_DRAW_FRAMEBUFFER_BINDING) != 0) {
            return;
        }
        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, this.frameBufferId);
        GL30C.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, GL11C.GL_COLOR_BUFFER_BIT, GL11C.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, 0);
        ci.cancel();
    }
}
