package com.misanthropy.collections_of_optimizations.mixin.biomeswevegone;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BwgTerrainChunkFilter;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.potionstudios.biomeswevegone.world.level.levelgen.customterrain.BasaltBarreraExtension", remap = false)
public abstract class MixinBasaltBarreraExtension {

    @Inject(
            method = "runBasaltBarreraExtension",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipChunksWithoutBasaltBarrera(ChunkAccess chunk,
                                                           WorldGenRegion region,
                                                           ChunkGenerator generator,
                                                           CallbackInfo ci) {
        if (CoOConfig.biomeswevegoneSkipForeignChunkTerrain && !BwgTerrainChunkFilter.hasBasaltBarrera(region, chunk)) {
            ci.cancel();
        }
    }
}
