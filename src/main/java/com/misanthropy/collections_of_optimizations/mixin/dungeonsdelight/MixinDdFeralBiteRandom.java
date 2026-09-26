package com.misanthropy.collections_of_optimizations.mixin.dungeonsdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Pseudo
@Mixin(targets = "net.yirmiri.dungeonsdelight.core.event.DDCommonEvents", remap = false)
public abstract class MixinDdFeralBiteRandom {

    @WrapOperation(
            method = "feralBiteAttack",
            at = @At(value = "NEW", target = "java/util/Random"),
            require = 0
    )
    private static Random coo$sharedRandom(Operation<Random> original) {
        if (CoOConfig.dungeonsdelightSharedFeralBiteRandom) {
            return ThreadLocalRandom.current();
        }
        return original.call();
    }
}
