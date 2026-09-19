package com.misanthropy.collections_of_optimizations.mixin.starcatcher;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.wdiscute.starcatcher.fishspotter.FishRadarLayer", remap = false)
public abstract class MixinStarcatcherFishRadarRefresh {

    @Unique
    private long coo$nextRadarScan;

    @Inject(
            method = "recalculate",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$throttleRadarScan(CallbackInfo ci) {
        int interval = CoOConfig.starcatcherFishRadarScanIntervalMs;
        if (interval <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < coo$nextRadarScan) {
            ci.cancel();
            return;
        }
        coo$nextRadarScan = now + interval;
    }
}
