package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.GeodePoints;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GeodeFeature.class)
public abstract class MixinGeodeFeatureNoise {

    @Redirect(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/synth/NormalNoise;getValue(DDD)D"),
            require = 0
    )
    private double coo$skipNoiseOutside(NormalNoise noise, double x, double y, double z, FeaturePlaceContext<GeodeConfiguration> context) {
        GeodeConfiguration configuration = context.config();
        double multiplier = configuration.noiseMultiplier;
        GeodePoints<Pair<BlockPos, Integer>> points = GeodePoints.centers();
        if (multiplier > 0.0D && points != null && !points.isEmpty()) {
            int n = points.size();
            if (points.outer == 0.0D) {
                points.outer = 1.0D / Math.sqrt(configuration.geodeLayerSettings.outerLayer + (double) n / (double) configuration.outerWallDistance.getMaxValue());
            }
            double sum = n * noise.maxValue() * multiplier + 1.0E-9D;
            for (int i = 0; i < n; i++) {
                Pair<BlockPos, Integer> point = points.get(i);
                BlockPos at = point.getFirst();
                double dx = x - at.getX();
                double dy = y - at.getY();
                double dz = z - at.getZ();
                sum += Mth.invSqrt(dx * dx + dy * dy + dz * dz + point.getSecond());
            }
            if (sum < points.outer) {
                return Double.NEGATIVE_INFINITY;
            }
        }
        return noise.getValue(x, y, z);
    }
}
