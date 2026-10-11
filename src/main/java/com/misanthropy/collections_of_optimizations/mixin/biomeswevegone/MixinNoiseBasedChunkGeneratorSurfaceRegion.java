package com.misanthropy.collections_of_optimizations.mixin.biomeswevegone;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BwgTerrainChunkFilter;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class MixinNoiseBasedChunkGeneratorSurfaceRegion {

    @Inject(
            method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V",
            at = @At("HEAD"),
            require = 0
    )
    private void coo$rememberSurfaceRegion(WorldGenRegion region, StructureManager structures, RandomState random,
                                           ChunkAccess chunk, CallbackInfo ci) {
        if (CoOConfig.biomeswevegoneSkipForeignChunkTerrain) {
            BwgTerrainChunkFilter.rememberSurfaceRegion(region);
        }
    }
}
