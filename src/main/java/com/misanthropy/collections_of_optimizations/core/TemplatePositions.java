package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;

public final class TemplatePositions {

    private static final int BITS = 7;
    private static final int SIZE = 1 << BITS;
    private static final BlockPos[] TABLE = new BlockPos[SIZE * SIZE * SIZE];

    private TemplatePositions() {
    }

    public static BlockPos of(int x, int y, int z) {
        if ((x | y | z) < 0 || x >= SIZE || y >= SIZE || z >= SIZE) {
            return new BlockPos(x, y, z);
        }
        int index = (y << (2 * BITS)) | (z << BITS) | x;
        BlockPos pos = TABLE[index];
        if (pos == null) {
            pos = new BlockPos(x, y, z);
            TABLE[index] = pos;
        }
        return pos;
    }
}
