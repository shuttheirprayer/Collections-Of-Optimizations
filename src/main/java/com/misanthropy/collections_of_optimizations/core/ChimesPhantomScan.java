package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class ChimesPhantomScan {

    private ChimesPhantomScan() {
    }

    public static boolean skip(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return false;
        }
        if (level.isClientSide) {
            return CoOConfig.chimesSkipClientPhantomScan;
        }
        int interval = CoOConfig.chimesPhantomScanInterval;
        if (interval <= 1) {
            return false;
        }
        long key = pos.asLong();
        int salt = (int) (key ^ (key >>> 32));
        return Math.floorMod(level.getGameTime() + salt, (long) interval) != 0L;
    }
}
