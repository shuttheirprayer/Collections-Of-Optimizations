package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.function.Predicate;

public final class SectionPrecheck {

    private SectionPrecheck() {
    }

    public static boolean mayContain(Level level, int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
                                     Predicate<BlockState> test) {
        if ((minY < level.getMinBuildHeight() || maxY >= level.getMaxBuildHeight())
                && test.test(Blocks.VOID_AIR.defaultBlockState())) {
            return true;
        }
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
                if (chunk == null) {
                    return true;
                }
                int fromSection = Math.max(minY >> 4, chunk.getMinSection());
                int toSection = Math.min(maxY >> 4, chunk.getMaxSection() - 1);
                for (int sectionY = fromSection; sectionY <= toSection; sectionY++) {
                    LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(sectionY));
                    if (section == null ? test.test(air) : section.maybeHas(test)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
