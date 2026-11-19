package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ReusedPositionalRandom;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.level.levelgen.SurfaceRules$VerticalGradientConditionSource$1VerticalGradientCondition")
public abstract class MixinVerticalGradientRandom {

    @Redirect(method = "compute", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/PositionalRandomFactory;at(III)Lnet/minecraft/util/RandomSource;"))
    private RandomSource coo$reusedRandom(PositionalRandomFactory factory, int x, int y, int z) {
        return CoOConfig.vanillaReuseGradientRandom ? ReusedPositionalRandom.at(factory, x, y, z) : factory.at(x, y, z);
    }
}
