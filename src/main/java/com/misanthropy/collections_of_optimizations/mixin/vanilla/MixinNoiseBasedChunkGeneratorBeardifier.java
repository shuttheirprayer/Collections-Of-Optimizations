package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BeardifierEmptiness;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class MixinNoiseBasedChunkGeneratorBeardifier {

    @ModifyArg(
            method = "createNoiseChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/NoiseChunk;forChunk(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/DensityFunctions$BeardifierOrMarker;Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/world/level/levelgen/Aquifer$FluidPicker;Lnet/minecraft/world/level/levelgen/blending/Blender;)Lnet/minecraft/world/level/levelgen/NoiseChunk;"
            ),
            index = 2,
            require = 0
    )
    private DensityFunctions.BeardifierOrMarker coo$skipEmptyBeardifier(DensityFunctions.BeardifierOrMarker beardifier) {
        DensityFunctions.BeardifierOrMarker empty = BeardifierEmptiness.emptyMarker();
        if (CoOConfig.vanillaSkipEmptyBeardifier
                && empty != null
                && beardifier instanceof Beardifier real
                && BeardifierEmptiness.isEmpty(real)) {
            return empty;
        }
        return beardifier;
    }
}
