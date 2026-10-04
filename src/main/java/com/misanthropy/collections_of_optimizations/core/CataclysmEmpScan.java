package com.misanthropy.collections_of_optimizations.core;

import com.github.L_Ender.cataclysm.init.ModBlocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.function.Predicate;

public final class CataclysmEmpScan {

    private static final Predicate<BlockState> IS_EMP = state -> state.is(ModBlocks.EMP.get());

    private CataclysmEmpScan() {
    }

    public static boolean mayContainEmp(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
        if (level.isDebug()) {
            return true;
        }
        int minY = Math.max(y0, level.getMinBuildHeight());
        int maxY = Math.min(y1, level.getMaxBuildHeight() - 1);
        if (minY > maxY) {
            return false;
        }
        for (int cz = z0 >> 4; cz <= z1 >> 4; cz++) {
            for (int cx = x0 >> 4; cx <= x1 >> 4; cx++) {
                LevelChunk chunk = level.getChunk(cx, cz);
                LevelChunkSection[] sections = chunk.getSections();
                for (int sy = minY >> 4; sy <= maxY >> 4; sy++) {
                    LevelChunkSection section = sections[chunk.getSectionIndexFromSectionY(sy)];
                    if (!section.hasOnlyAir() && section.getStates().maybeHas(IS_EMP)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
