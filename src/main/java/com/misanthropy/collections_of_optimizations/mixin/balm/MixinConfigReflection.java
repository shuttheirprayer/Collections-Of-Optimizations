package com.misanthropy.collections_of_optimizations.mixin.balm;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.blay09.mods.balm.api.network.ConfigReflection;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = ConfigReflection.class, remap = false)
public abstract class MixinConfigReflection {

    @Unique
    private static final Map<Class<?>, ResourceLocation> COO$IDENTIFIERS = new ConcurrentHashMap<>();

    @WrapMethod(method = "getIdentifier", require = 0)
    private static ResourceLocation coo$cachedIdentifier(Class<?> clazz, Operation<ResourceLocation> original) {
        if (!CoOConfig.balmCacheConfigIdentifier) {
            return original.call(clazz);
        }
        ResourceLocation cached = COO$IDENTIFIERS.get(clazz);
        if (cached != null) {
            return cached;
        }
        ResourceLocation computed = original.call(clazz);
        COO$IDENTIFIERS.put(clazz, computed);
        return computed;
    }
}
