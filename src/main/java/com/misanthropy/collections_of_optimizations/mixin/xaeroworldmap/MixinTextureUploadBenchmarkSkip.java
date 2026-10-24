package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xaero.map.graphics.TextureUploadBenchmark;

@Mixin(value = TextureUploadBenchmark.class, remap = false)
public abstract class MixinTextureUploadBenchmarkSkip {

    @Unique
    private static final long[] coo$ESTIMATED_NANOS = {200_000L, 200_000L, 200_000L, 500_000L, 1_000_000L, 200_000L, 200_000L};

    @Shadow
    private boolean[] finished;

    @Shadow
    private long[] results;

    @Shadow
    private long[] accumulators;

    @Shadow
    private int[] totals;

    @Overwrite
    public boolean isFinished(int type) {
        return CoOConfig.xaeroworldmapSkipUploadBenchmark || this.finished[type];
    }

    @Overwrite
    public long getAverage(int type) {
        if (this.finished[type]) {
            return this.results[type];
        }
        if (this.totals[type] > 0) {
            return this.accumulators[type] / (long) this.totals[type];
        }
        return type < coo$ESTIMATED_NANOS.length ? coo$ESTIMATED_NANOS[type] : 1_000_000L;
    }
}
