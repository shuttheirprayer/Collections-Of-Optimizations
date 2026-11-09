package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.MapWriter;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.MapLayer;

import java.util.function.BiConsumer;

@Mixin(value = MapWriter.class, remap = false)
public abstract class MixinMapWriterLayerMarkOnce {

    @Unique
    private int coo$pass;
    @Unique
    private int coo$markedPass = -1;
    @Unique
    private LayeredRegionManager coo$markedManager;
    @Unique
    private int coo$markedLayer;
    @Unique
    private int coo$markedX;
    @Unique
    private int coo$markedZ;

    @Inject(method = "onRender", at = @At("HEAD"), require = 0)
    private void coo$nextPass(CallbackInfo ci) {
        this.coo$pass++;
    }

    @WrapOperation(method = "writeChunk", at = @At(value = "INVOKE", target = "Lxaero/map/region/LayeredRegionManager;applyToEachLoadedLayer(Ljava/util/function/BiConsumer;)V"), require = 0)
    private void coo$markOtherLayersOnce(LayeredRegionManager manager, BiConsumer<Integer, MapLayer> consumer, Operation<Void> original,
                                         @Local(argsOnly = true, ordinal = 3) int layer, @Local(argsOnly = true, ordinal = 4) int tileChunkX,
                                         @Local(argsOnly = true, ordinal = 5) int tileChunkZ) {
        if (!CoOConfig.xaeroworldmapMarkLayersOnce) {
            original.call(manager, consumer);
            return;
        }
        int regionX = tileChunkX >> 3;
        int regionZ = tileChunkZ >> 3;
        if (this.coo$markedPass == this.coo$pass && this.coo$markedManager == manager && this.coo$markedLayer == layer
                && this.coo$markedX == regionX && this.coo$markedZ == regionZ) {
            return;
        }
        original.call(manager, consumer);
        this.coo$markedPass = this.coo$pass;
        this.coo$markedManager = manager;
        this.coo$markedLayer = layer;
        this.coo$markedX = regionX;
        this.coo$markedZ = regionZ;
    }
}
