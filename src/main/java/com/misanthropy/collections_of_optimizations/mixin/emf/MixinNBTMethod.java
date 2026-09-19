package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.EmfNbtTickCache;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import traben.entity_model_features.models.animation.math.methods.emf.NBTMethod;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;

import java.util.UUID;

@Mixin(value = NBTMethod.class, remap = false)
public abstract class MixinNBTMethod {

    @WrapMethod(method = "nbtMethodStatic", require = 0)
    private static boolean coo$cachePerTick(String nbtKey, String nbtQuery, Operation<Boolean> original) {
        EMFEntityRenderState state = CoOConfig.emfCacheNbtPerTick ? EMFState.state() : null;
        if (state == null) {
            return original.call(nbtKey, nbtQuery);
        }
        UUID uuid = state.uuid();
        if (uuid == null) {
            return original.call(nbtKey, nbtQuery);
        }
        Object2ByteOpenHashMap<UUID> byEntity = EmfNbtTickCache.forQuery(nbtKey, nbtQuery);
        byte cached = byEntity.getByte(uuid);
        if (cached >= 0) {
            return cached == 1;
        }
        boolean result = original.call(nbtKey, nbtQuery);
        byEntity.put(uuid, result ? (byte) 1 : (byte) 0);
        return result;
    }
}
