package com.misanthropy.collections_of_optimizations.mixin.balm;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.blay09.mods.balm.api.network.ConfigReflection;
import net.blay09.mods.balm.common.config.AbstractBalmConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = AbstractBalmConfig.class, remap = false)
public abstract class MixinAbstractBalmConfig {

    @Shadow
    @Final
    private Map<ResourceLocation, Object> activeReflectionConfigs;

    @WrapMethod(method = "getActiveConfig(Ljava/lang/Class;)Ljava/lang/Object;", require = 0)
    private <T> T coo$activeConfigHitFirst(Class<T> clazz, Operation<T> original) {
        if (!CoOConfig.balmCacheConfigIdentifier) {
            return original.call(clazz);
        }
        Object hit = this.activeReflectionConfigs.get(ConfigReflection.getIdentifier(clazz));
        if (hit != null) {
            return (T) hit;
        }
        return original.call(clazz);
    }
}
