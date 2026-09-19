package com.misanthropy.collections_of_optimizations.mixin.goety;

import com.Polarice3.Goety.common.effects.brew.BrewEffects;
import com.Polarice3.Goety.compat.patchouli.BrewingSacrificeProcessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.GoetyBrewEffectsCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BrewingSacrificeProcessor.class, remap = false)
public abstract class MixinBrewingSacrificeProcessor {

    @WrapOperation(
            method = {"setup", "process"},
            at = @At(
                    value = "NEW",
                    target = "()Lcom/Polarice3/Goety/common/effects/brew/BrewEffects;"
            ),
            require = 0
    )
    private BrewEffects coo$shareBrewEffects(Operation<BrewEffects> original) {
        if (!CoOConfig.goetyCacheBrewEffects) {
            return original.call();
        }
        return GoetyBrewEffectsCache.get();
    }
}
