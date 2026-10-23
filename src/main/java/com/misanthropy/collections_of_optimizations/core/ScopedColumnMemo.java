package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;

public final class ScopedColumnMemo {

    private static final ThreadLocal<ScopedColumnMemo> CURRENT = ThreadLocal.withInitial(ScopedColumnMemo::new);

    private final Long2ObjectOpenHashMap<NoiseColumn> columns = new Long2ObjectOpenHashMap<>();
    private int depth;
    private ChunkGenerator generator;
    private LevelHeightAccessor heights;
    private RandomState random;

    public static ScopedColumnMemo open() {
        ScopedColumnMemo memo = CURRENT.get();
        memo.depth++;
        return memo;
    }

    public void close() {
        if (--this.depth == 0) {
            this.columns.clear();
            this.columns.trim(64);
            this.generator = null;
            this.heights = null;
            this.random = null;
        }
    }

    public static NoiseColumn column(ChunkGenerator generator, int x, int z, LevelHeightAccessor heights, RandomState random) {
        ScopedColumnMemo memo = CURRENT.get();
        if (memo.depth == 0) {
            return generator.getBaseColumn(x, z, heights, random);
        }
        if (memo.generator != generator || memo.heights != heights || memo.random != random) {
            memo.columns.clear();
            memo.generator = generator;
            memo.heights = heights;
            memo.random = random;
        }
        long key = ChunkPos.asLong(x, z);
        NoiseColumn column = memo.columns.get(key);
        if (column == null) {
            column = generator.getBaseColumn(x, z, heights, random);
            memo.columns.put(key, column);
        }
        return column;
    }
}
