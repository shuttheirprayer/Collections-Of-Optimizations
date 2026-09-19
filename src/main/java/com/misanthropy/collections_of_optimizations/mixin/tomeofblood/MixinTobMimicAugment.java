package com.misanthropy.collections_of_optimizations.mixin.tomeofblood;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "com.mystchonky.tomeofblood.common.glyphs.AugmentMimic", remap = false)
public abstract class MixinTobMimicAugment {

    @WrapOperation(
            method = "applyModifiers",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;"
            ),
            require = 0
    )
    private Object coo$guardMimicIndex(List<?> recipe, int index, Operation<Object> original) {
        if (!CoOConfig.tomeofbloodFixMimicAugmentIndex) {
            return original.call(recipe, index);
        }
        if (recipe == null || index < 0 || index >= recipe.size()) {
            return null;
        }
        return original.call(recipe, index);
    }
}
