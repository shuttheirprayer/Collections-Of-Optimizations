package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.MapProcessor;
import xaero.map.gui.GuiMap;

@Mixin(value = MapProcessor.class, remap = false)
public abstract class MixinMapProcessorRate {

    @Unique
    private long coo$lastRenderProcess;

    @Inject(method = "onRenderProcess", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$capRenderProcessRate(Minecraft mc, CallbackInfo ci) {
        int interval = CoOConfig.xaeroworldmapRenderProcessInterval;
        if (interval <= 0 || mc.screen instanceof GuiMap) {
            return;
        }

        long now = System.nanoTime() / 1_000_000L;
        if (now - this.coo$lastRenderProcess < interval) {
            ci.cancel();
            return;
        }
        this.coo$lastRenderProcess = now;
    }
}
