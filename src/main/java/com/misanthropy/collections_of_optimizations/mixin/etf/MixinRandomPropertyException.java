package com.misanthropy.collections_of_optimizations.mixin.etf;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;

@Mixin(value = RandomProperty.RandomPropertyException.class, remap = false)
public abstract class MixinRandomPropertyException extends Exception {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return CoOConfig.etfStacklessPropertyExceptions ? this : super.fillInStackTrace();
    }
}
