package com.misanthropy.collections_of_optimizations.mixin.pehkui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import virtuoel.pehkui.api.ScaleData;

@Mixin(value = ScaleData.class, remap = false)
public abstract class MixinScaleDataSteadyBaseScale {

    @Shadow
    private float baseScale;

    @Shadow
    private float initialScale;

    @Shadow
    private float targetScale;

    @WrapOperation(
            method = "getBaseScale(F)F",
            at = @At(value = "INVOKE", target = "Lvirtuoel/pehkui/api/ScaleData;calculateScaleForTick(IF)F"),
            require = 0
    )
    private float coo$steadyBaseScale(ScaleData data, int ticks, float delta, Operation<Float> original) {
        if (CoOConfig.pehkuiSteadyBaseScale && ticks == 0 && this.baseScale == this.targetScale && this.initialScale == this.targetScale) {
            return this.baseScale;
        }
        return original.call(data, ticks, delta);
    }
}
