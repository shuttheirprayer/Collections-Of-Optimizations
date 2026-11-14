package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.OutlineOverlaySkip;
import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GlStateManager.class, remap = false)
public abstract class MixinGlStateManagerOutlineWatch {

    @ModifyVariable(method = "_glBindFramebuffer", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static int coo$watchFramebuffer(int framebuffer) {
        return OutlineOverlaySkip.framebufferBound(framebuffer);
    }

    @ModifyVariable(method = "_bindTexture", at = @At("HEAD"), argsOnly = true)
    private static int coo$watchTexture(int texture) {
        return OutlineOverlaySkip.textureBound(texture);
    }
}
