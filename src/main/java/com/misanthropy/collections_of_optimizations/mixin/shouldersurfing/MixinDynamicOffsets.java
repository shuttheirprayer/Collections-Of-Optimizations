package com.misanthropy.collections_of_optimizations.mixin.shouldersurfing;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "com.github.exopandora.shouldersurfing.client.event.handler.ComputeTargetCameraOffsetEventHandlerImpl$DynamicOffsets",
        remap = false)
public abstract class MixinDynamicOffsets {

    @ModifyConstant(
            method = "calcDynamicOffsets",
            constant = @Constant(doubleValue = 0.03125),
            require = 0,
            expect = 0
    )
    private static double coo$sweepStep(double original) {
        return Math.max(original, CoOConfig.shouldersurfingCameraSweepStep);
    }
}
