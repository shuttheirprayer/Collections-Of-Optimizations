package com.misanthropy.collections_of_optimizations.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

@Pseudo
@Mixin(targets = "de.keksuccino.fancymenu.util.InternetAvailabilityMonitor", remap = false)
public abstract class MixinInternetAvailabilityMonitor {

    @Unique
    private long coo$lastProbeNanos = Long.MIN_VALUE;

    @WrapMethod(method = "refresh", require = 0)
    private void coo$throttleProbe(long generation, Operation<Void> original) {
        long minGap = CoOConfig.fancymenuInternetProbeSeconds * 1_000_000_000L;
        long now = System.nanoTime();
        if (minGap > 0 && coo$lastProbeNanos != Long.MIN_VALUE && now - coo$lastProbeNanos < minGap) {
            return;
        }
        coo$lastProbeNanos = now;
        original.call(generation);
    }
}
