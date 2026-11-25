package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.XaeroTileRefresh;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xaero.map.MapWriter;
import xaero.map.region.MapTileChunk;

@Mixin(value = MapWriter.class, remap = false)
public abstract class MixinMapWriterRefreshPace {

    @WrapOperation(method = "writeChunk", at = @At(value = "INVOKE", target = "Lxaero/map/region/MapTileChunk;wasChanged()Z", ordinal = 0), require = 0)
    private boolean coo$paceRefresh(MapTileChunk chunk, Operation<Boolean> original) {
        if (!original.call(chunk)) {
            return false;
        }
        int pace = CoOConfig.xaeroworldmapTileRefreshMillis;
        if (pace > 0) {
            XaeroTileRefresh refresh = (XaeroTileRefresh) chunk;
            long now = Util.getMillis();
            if (now - refresh.coo$refreshed() < pace) {
                return false;
            }
            refresh.coo$refreshed(now);
        }
        return true;
    }
}
