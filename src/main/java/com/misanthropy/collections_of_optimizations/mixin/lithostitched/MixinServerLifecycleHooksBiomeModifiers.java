package com.misanthropy.collections_of_optimizations.mixin.lithostitched;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = ServerLifecycleHooks.class, priority = 1500, remap = false)
public abstract class MixinServerLifecycleHooksBiomeModifiers {

    @Unique
    private static List<BiomeModifier> coo$mergedSource;

    @Unique
    private static List<BiomeModifier> coo$merged;

    @WrapOperation(
            method = "lambda$runModifiers$5",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/server/ServerLifecycleHooks;(Ljava/util/List;)Ljava/util/List;"
            ),
            require = 0,
            expect = 0
    )
    private static List<BiomeModifier> coo$reuseMergedBiomeModifiers(List<BiomeModifier> biomeModifiers, Operation<List<BiomeModifier>> original) {
        if (!CoOConfig.lithostitchedCacheBiomeModifierList) {
            return original.call(biomeModifiers);
        }
        if (coo$mergedSource != biomeModifiers) {
            coo$merged = original.call(biomeModifiers);
            coo$mergedSource = biomeModifiers;
        }
        return coo$merged;
    }
}
