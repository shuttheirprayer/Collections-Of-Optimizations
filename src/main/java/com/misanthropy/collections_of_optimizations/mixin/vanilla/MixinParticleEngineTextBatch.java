package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.ParticleTextBatch;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class MixinParticleEngineTextBatch {

    private static final String RENDER = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V";

    @Inject(method = RENDER, at = @At("HEAD"), remap = false, require = 0)
    private void coo$beginTextBatch(CallbackInfo ci) {
        ParticleTextBatch.begin();
    }

    @Inject(
            method = RENDER,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleRenderType;end(Lcom/mojang/blaze3d/vertex/Tesselator;)V",
                    shift = At.Shift.AFTER,
                    remap = true
            ),
            remap = false,
            require = 0
    )
    private void coo$flushTextBatch(CallbackInfo ci) {
        ParticleTextBatch.flush();
    }

    @Inject(method = RENDER, at = @At("RETURN"), remap = false, require = 0)
    private void coo$endTextBatch(CallbackInfo ci) {
        ParticleTextBatch.end();
    }
}
