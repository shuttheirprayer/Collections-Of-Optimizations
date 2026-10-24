package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRarity;
import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CaveBiomeQuadCache;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ACBiomeRarity.class, remap = false)
public abstract class MixinACBiomeRarity {

    @Shadow @Final private static PerlinSimplexNoise NOISE_X;
    @Shadow @Final private static PerlinSimplexNoise NOISE_Z;
    @Shadow @Final private static VoronoiGenerator VORONOI_GENERATOR;
    @Shadow private static double biomeSize;
    @Shadow private static double seperationDistance;

    @Overwrite(remap = false)
    public static VoronoiGenerator.VoronoiInfo getRareBiomeInfoForQuad(long worldSeed, int x, int z) {
        double randomness = AlexsCaves.COMMON_CONFIG.caveBiomeWidthRandomness.get();
        boolean memo = CoOConfig.alexscavesMemoRareBiomeQuads;
        if (memo) {
            VoronoiGenerator.VoronoiInfo hit = CaveBiomeQuadCache.get().lookup(worldSeed, randomness, x, z);
            if (hit != null) {
                return hit == CaveBiomeQuadCache.ABSENT ? null : hit;
            }
        }
        VORONOI_GENERATOR.setSeed(worldSeed);
        double sampleX = (double) x / seperationDistance;
        double sampleZ = (double) z / seperationDistance;
        double positionOffsetX = randomness * NOISE_X.getValue(sampleX, sampleZ, false);
        double positionOffsetZ = randomness * NOISE_Z.getValue(sampleX, sampleZ, false);
        VoronoiGenerator.VoronoiInfo info = VORONOI_GENERATOR.get2(sampleX + positionOffsetX, sampleZ + positionOffsetZ);
        VoronoiGenerator.VoronoiInfo result = info.distance() < biomeSize / seperationDistance ? info : null;
        if (memo) {
            CaveBiomeQuadCache.get().store(worldSeed, randomness, x, z, result);
        }
        return result;
    }

    @Inject(
            method = "init",
            at = @At("HEAD"),
            require = 0
    )
    private static void coo$invalidateOnInit(CallbackInfo ci) {
        CaveBiomeQuadCache.invalidate();
    }
}
