package com.misanthropy.collections_of_optimizations.mixin.byepregen;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.moepus.byepregen.worldgen.feature.FastBlockPredicateOptimizer;
import com.moepus.byepregen.worldgen.feature.WorldGenRegionSectionCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FastBlockPredicateOptimizer.class, remap = false)
public abstract class MixinFastBlockPredicateOptimizer {

    @Overwrite
    private static LevelChunkSection getSection(WorldGenLevel level, int sectionX, int sectionIndex, int sectionZ) {
        ChunkAccess chunk;
        if (CoOConfig.byepregenDirectSectionLookup && level instanceof WorldGenRegion region) {
            chunk = region.getChunk(sectionX, sectionZ, ChunkStatus.EMPTY, true);
        } else if (level instanceof WorldGenRegionSectionCache cache) {
            return cache.byepregen$getCachedSection(sectionX, sectionIndex, sectionZ);
        } else {
            chunk = level.getChunk(sectionX, sectionZ, ChunkStatus.EMPTY, true);
        }
        return chunk == null ? null : chunk.getSection(sectionIndex);
    }
}
