package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DensityGraphReuse;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseChunk.class)
public abstract class MixinNoiseChunkSurfaceShare {

    @Unique
    private DensityGraphReuse.SurfaceTable coo$surfaceTable;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void coo$bindSurfaceTable(int cellCountXZ, RandomState randomState, int firstBlockX, int firstBlockZ,
                                      NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifier,
                                      NoiseGeneratorSettings generatorSettings, Aquifer.FluidPicker fluidPicker,
                                      Blender blender, CallbackInfo ci) {
        if (CoOConfig.vanillaShareSurfaceEstimates && blender == Blender.empty()) {
            this.coo$surfaceTable = DensityGraphReuse.surfaceTable(randomState, noiseSettings);
        }
    }

    @WrapMethod(method = "computePreliminarySurfaceLevel")
    private int coo$sharedSurfaceLevel(long column, Operation<Integer> original) {
        DensityGraphReuse.SurfaceTable table = this.coo$surfaceTable;
        return table == null ? original.call(column) : table.get(column, key -> original.call(key));
    }
}
