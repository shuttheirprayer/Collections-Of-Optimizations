package com.misanthropy.collections_of_optimizations.mixin.adastra;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PlanetDefaultsMemo;
import earth.terrarium.adastra.common.systems.GravityApiImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(value = GravityApiImpl.class, remap = false)
public abstract class MixinGravityApiImpl {

    @WrapMethod(method = "getGravity(Lnet/minecraft/resources/ResourceKey;)F", require = 0)
    private float coo$memoDimensionGravity(ResourceKey<Level> dimension, Operation<Float> original) {
        if (!CoOConfig.adastraMemoPlanetDefaults || dimension == null) {
            return original.call(dimension);
        }
        Map<ResourceKey<Level>, Float> memo = PlanetDefaultsMemo.gravity();
        Float hit = memo.get(dimension);
        if (hit != null) {
            return hit;
        }
        float value = original.call(dimension);
        memo.put(dimension, value);
        return value;
    }
}
