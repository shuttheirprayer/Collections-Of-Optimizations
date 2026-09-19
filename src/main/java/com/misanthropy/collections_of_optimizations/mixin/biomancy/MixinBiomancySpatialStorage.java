package com.misanthropy.collections_of_optimizations.mixin.biomancy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BiomancySpatialState;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Pseudo
@Mixin(targets = "com.github.elenterius.biomancy.world.spatial.SpatialShapeStorage", remap = false)
public abstract class MixinBiomancySpatialStorage {

    @Inject(method = "getInstance", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$reuseStorageInstance(ServerLevel level, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.biomancyCacheSpatialStorage || level == null) {
            return;
        }
        Object cached = BiomancySpatialState.cachedStorage(level);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "getInstance", at = @At("TAIL"), require = 0)
    private static void coo$captureStorageInstance(ServerLevel level, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.biomancyCacheSpatialStorage || level == null) {
            return;
        }
        BiomancySpatialState.putStorage(level, cir.getReturnValue());
    }

    @Inject(method = "getShapes", at = @At("TAIL"), require = 0)
    private void coo$captureShapeMap(String levelKey, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.biomancySkipEmptySpawnQuery) {
            return;
        }
        Object shapes = cir.getReturnValue();
        if (shapes instanceof Map<?, ?> map) {
            BiomancySpatialState.captureShapes(this, levelKey, map);
        }
    }

    @Inject(method = "close", at = @At("HEAD"), require = 0)
    private void coo$releaseStorageInstance(CallbackInfo ci) {
        BiomancySpatialState.release(this);
    }
}
