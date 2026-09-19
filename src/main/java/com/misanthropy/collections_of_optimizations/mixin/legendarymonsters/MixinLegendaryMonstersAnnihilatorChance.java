package com.misanthropy.collections_of_optimizations.mixin.legendarymonsters;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.event.ForgeEvents", remap = false)
public abstract class MixinLegendaryMonstersAnnihilatorChance {

    @ModifyExpressionValue(
            method = "onLivingHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt()I"
            ),
            remap = true,
            require = 0
    )
    private static int coo$fixAnnihilatorProcChance(int original) {
        if (!CoOConfig.legendarymonstersFixAnnihilatorProcChance) {
            return original;
        }
        return (original & 3) == 0 ? 0 : 1;
    }
}
