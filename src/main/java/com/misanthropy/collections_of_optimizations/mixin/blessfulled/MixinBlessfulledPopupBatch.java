package com.misanthropy.collections_of_optimizations.mixin.blessfulled;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ParticleTextBatch;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "org.aqutheseal.blessfulled.particle.DamagePopupParticle", remap = false)
public abstract class MixinBlessfulledPopupBatch {

    @WrapOperation(
            method = "m_5744_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;m_109911_()V"
            ),
            remap = false,
            require = 0
    )
    private void coo$batchPopupFlush(MultiBufferSource.BufferSource buffer, Operation<Void> original) {
        if (!CoOConfig.blessfulledBatchPopupFlush || !ParticleTextBatch.defer(buffer)) {
            original.call(buffer);
        }
    }
}
