package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ColumnHeightTable;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class MixinNoiseBasedChunkGeneratorColumnHeights {

    @Unique
    private volatile ColumnHeightTable[] coo$columnHeights;

    @WrapMethod(method = "getBaseHeight", require = 0)
    private int coo$sharedBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState,
                                     Operation<Integer> original) {
        if (!CoOConfig.vanillaShareColumnHeights || randomState == null || level == null
                || ((Object) this).getClass() != NoiseBasedChunkGenerator.class) {
            return original.call(x, z, type, level, randomState);
        }
        long key = ColumnHeightTable.key(x, z, type);
        if (key == Long.MIN_VALUE) {
            return original.call(x, z, type, level, randomState);
        }
        int minBuildHeight = level.getMinBuildHeight();
        int maxBuildHeight = level.getMaxBuildHeight();
        ColumnHeightTable[] tables = this.coo$columnHeights;
        ColumnHeightTable table = ColumnHeightTable.find(tables, randomState, minBuildHeight, maxBuildHeight);
        if (table == null) {
            table = new ColumnHeightTable(randomState, minBuildHeight, maxBuildHeight);
            this.coo$columnHeights = ColumnHeightTable.with(tables, table);
        }
        int known = table.get(key);
        if (known != ColumnHeightTable.ABSENT) {
            return known;
        }
        int value = original.call(x, z, type, level, randomState);
        table.put(key, value);
        return value;
    }
}
