package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public final class MguInhibitorScan {

    public static final int UNKNOWN = -1;
    public static final int ABSENT = 0;
    public static final int PRESENT = 1;

    private static boolean resolved;
    private static boolean available;
    private static Class<?> onClass;
    private static Class<?> offClass;
    private static Predicate<BlockState> predicate;

    private MguInhibitorScan() {
    }

    private static boolean ready() {
        if (!resolved) {
            resolved = true;
            try {
                ClassLoader loader = MguInhibitorScan.class.getClassLoader();
                onClass = Class.forName("mob_grinding_utils.blocks.BlockEnderInhibitorOn", false, loader);
                offClass = Class.forName("mob_grinding_utils.blocks.BlockEnderInhibitorOff", false, loader);
                predicate = MguInhibitorScan::matches;
                available = true;
            } catch (Throwable ignored) {
                onClass = null;
                offClass = null;
                predicate = null;
                available = false;
            }
        }
        return available;
    }

    private static boolean matches(BlockState state) {
        Block block = state.getBlock();
        return onClass.isInstance(block) && !offClass.isInstance(block);
    }

    public static int scan(Level level, AABB box) {
        if (level == null || box == null || !ready()) {
            return UNKNOWN;
        }

        int minX = Mth.floor(box.minX);
        int maxX = Mth.floor(box.maxX);
        int minY = Mth.floor(box.minY);
        int maxY = Mth.floor(box.maxY);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.floor(box.maxZ);

        if (minX >= maxX || minY >= maxY || minZ >= maxZ) {
            return ABSENT;
        }

        int lastX = maxX - 1;
        int lastY = maxY - 1;
        int lastZ = maxZ - 1;

        Predicate<BlockState> test = predicate;

        for (int chunkX = minX >> 4; chunkX <= lastX >> 4; chunkX++) {
            int x0 = Math.max(minX, chunkX << 4);
            int x1 = Math.min(lastX, (chunkX << 4) + 15);

            for (int chunkZ = minZ >> 4; chunkZ <= lastZ >> 4; chunkZ++) {
                ChunkAccess chunk;
                try {
                    chunk = level.getChunk(chunkX, chunkZ);
                } catch (Throwable ignored) {
                    return UNKNOWN;
                }
                if (chunk == null) {
                    return UNKNOWN;
                }

                int z0 = Math.max(minZ, chunkZ << 4);
                int z1 = Math.min(lastZ, (chunkZ << 4) + 15);

                int fromSection = Math.max(minY >> 4, chunk.getMinSection());
                int toSection = Math.min(lastY >> 4, chunk.getMaxSection() - 1);

                for (int sectionY = fromSection; sectionY <= toSection; sectionY++) {
                    LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(sectionY));
                    if (section == null || section.hasOnlyAir() || !section.maybeHas(test)) {
                        continue;
                    }

                    int y0 = Math.max(minY, sectionY << 4);
                    int y1 = Math.min(lastY, (sectionY << 4) + 15);

                    for (int x = x0; x <= x1; x++) {
                        for (int y = y0; y <= y1; y++) {
                            for (int z = z0; z <= z1; z++) {
                                if (test.test(section.getBlockState(x & 15, y & 15, z & 15))) {
                                    return PRESENT;
                                }
                            }
                        }
                    }
                }
            }
        }

        return ABSENT;
    }
}
