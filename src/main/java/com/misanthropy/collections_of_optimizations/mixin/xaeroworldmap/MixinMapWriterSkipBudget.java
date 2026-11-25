package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.MapProcessor;
import xaero.map.MapWriter;
import xaero.map.gui.GuiMap;

@Mixin(value = MapWriter.class, remap = false)
public abstract class MixinMapWriterSkipBudget {

    @Shadow
    private MapProcessor mapProcessor;
    @Unique
    private long coo$lastPass;
    @Unique
    private int coo$skipsLeft;

    @Inject(method = "onRender", at = @At("HEAD"), require = 0)
    private void coo$refillSkips(CallbackInfo ci) {
        int perMilli = CoOConfig.xaeroworldmapSkippedChunksPerMilli;
        long now = System.nanoTime();
        double millis = (now - this.coo$lastPass) / 1_000_000.0D;
        this.coo$lastPass = now;
        this.coo$skipsLeft = perMilli <= 0 || Minecraft.getInstance().screen instanceof GuiMap || this.mapProcessor.isForceFastWritingConfig()
                ? Integer.MAX_VALUE : (int) Mth.clamp(millis * perMilli, 32.0D, 800.0D);
    }

    @ModifyExpressionValue(
            method = "onRender",
            at = @At(value = "INVOKE", target = "Lxaero/map/MapWriter;writeMap(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Registry;DDDLnet/minecraft/core/Registry;Lxaero/map/biome/BiomeColorCalculator;Lxaero/map/region/OverlayManager;ZZZZZLnet/minecraft/core/BlockPos$MutableBlockPos;Lxaero/map/biome/BlockTintProvider;ILxaero/map/region/MapUpdateFastConfig;)Z"),
            require = 0
    )
    private boolean coo$countSkippedChunk(boolean skipped) {
        return skipped && --this.coo$skipsLeft > 0;
    }
}
