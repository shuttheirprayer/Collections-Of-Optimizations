package com.misanthropy.collections_of_optimizations.mixin.modularrouters;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "me.desht.modularrouters.logic.compiled.CompiledVacuumModule", remap = false)
public abstract class MixinVacuumRegulatorClamp {

    @ModifyExpressionValue(
            method = "handleItemMode",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;min(II)I",
                    ordinal = 1,
                    remap = false
            ),
            require = 0
    )
    private int coo$clampVacuumPickupAmount(int amount) {
        if (!CoOConfig.modularroutersClampVacuumRegulator) {
            return amount;
        }
        return amount < 0 ? 0 : amount;
    }
}
