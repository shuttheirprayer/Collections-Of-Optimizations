package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.PausedWorldFrame;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 1500)
public abstract class MixinPausedWorldFrame {
    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
    private void coo$reusePausedFrame(float partialTick, long finishNanos, PoseStack poseStack, CallbackInfo ci) {
        if (PausedWorldFrame.reuse(minecraft)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void coo$keepPausedFrame(float partialTick, long finishNanos, PoseStack poseStack, CallbackInfo ci) {
        PausedWorldFrame.drawn(minecraft);
    }
}
