package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = BakedGeoModel.class, remap = false)
public abstract class MixinBakedGeoModel {

    @Unique
    private volatile Map<String, Optional<GeoBone>> coo$boneLookup;

    @WrapMethod(method = "getBone", require = 0)
    private Optional<GeoBone> coo$cacheBoneLookup(String name, Operation<Optional<GeoBone>> original) {
        if (!CoOConfig.geckolibCacheBoneLookup) {
            return original.call(name);
        }
        Map<String, Optional<GeoBone>> cache = this.coo$boneLookup;
        if (cache != null) {
            Optional<GeoBone> hit = cache.get(name);
            if (hit != null) {
                return hit;
            }
        }
        Optional<GeoBone> result = original.call(name);
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
            this.coo$boneLookup = cache;
        }
        if (result != null) {
            cache.put(name, result);
        }
        return result;
    }
}
