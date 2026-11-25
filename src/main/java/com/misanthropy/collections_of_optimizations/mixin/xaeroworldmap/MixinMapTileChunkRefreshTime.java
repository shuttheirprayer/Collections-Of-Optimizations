package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.core.XaeroTileRefresh;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xaero.map.region.MapTileChunk;

@Mixin(value = MapTileChunk.class, remap = false)
public abstract class MixinMapTileChunkRefreshTime implements XaeroTileRefresh {

    @Unique
    private long coo$refreshed;

    @Override
    public long coo$refreshed() {
        return this.coo$refreshed;
    }

    @Override
    public void coo$refreshed(long millis) {
        this.coo$refreshed = millis;
    }
}
