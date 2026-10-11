package com.misanthropy.collections_of_optimizations.mixin.adastra;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PlanetDefaultsMemo;
import earth.terrarium.adastra.common.systems.TemperatureApiImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(value = TemperatureApiImpl.class, remap = false)
public abstract class MixinTemperatureApiImpl {

    @WrapMethod(method = "getTemperature(Lnet/minecraft/resources/ResourceKey;)S", require = 0)
    private short coo$memoDimensionTemperature(ResourceKey<Level> dimension, Operation<Short> original) {
        if (!CoOConfig.adastraMemoPlanetDefaults || dimension == null) {
            return original.call(dimension);
        }
        Map<ResourceKey<Level>, Short> memo = PlanetDefaultsMemo.temperature();
        Short hit = memo.get(dimension);
        if (hit != null) {
            return hit;
        }
        short value = original.call(dimension);
        memo.put(dimension, value);
        return value;
    }
}
