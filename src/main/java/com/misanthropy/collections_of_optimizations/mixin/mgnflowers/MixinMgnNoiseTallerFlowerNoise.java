package com.misanthropy.collections_of_optimizations.mixin.mgnflowers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Pseudo
@Mixin(targets = "io.github.magishanpixel.mgnflowers.worldgen.features.NoiseTallerFlowerFeature", remap = false)
public abstract class MixinMgnNoiseTallerFlowerNoise {

    @Unique
    private static final Map<Object, NormalNoise> coo$noiseByConfig = new ConcurrentHashMap<>();

    @WrapOperation(
            method = "m_142674_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/synth/NormalNoise;m_230511_(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/synth/NormalNoise$NoiseParameters;)Lnet/minecraft/world/level/levelgen/synth/NormalNoise;"
            ),
            require = 0
    )
    private NormalNoise coo$reuseNoise(RandomSource random, NormalNoise.NoiseParameters parameters,
                                       Operation<NormalNoise> original,
                                       @Local(argsOnly = true) FeaturePlaceContext<?> context) {
        if (!CoOConfig.mgnflowersReuseFlowerNoise) {
            return original.call(random, parameters);
        }
        return coo$noiseByConfig.computeIfAbsent(context.config(), config -> original.call(random, parameters));
    }
}
