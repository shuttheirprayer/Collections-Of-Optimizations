package com.misanthropy.collections_of_optimizations.mixin.terrablender;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TerrablenderLazyRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(value = NamespacedSurfaceRuleSource.class, remap = false)
public abstract class MixinNamespacedSourceLazy {

    @ModifyArg(
            method = "apply(Lnet/minecraft/world/level/levelgen/SurfaceRules$Context;)Lnet/minecraft/world/level/levelgen/SurfaceRules$SurfaceRule;",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V"),
            require = 0
    )
    private Consumer<Map.Entry<String, ?>> coo$buildNamespacesOnUse(Consumer<Map.Entry<String, ?>> consumer) {
        if (!CoOConfig.terrablenderLazyNamespaceRules) {
            return consumer;
        }
        return entry -> consumer.accept(TerrablenderLazyRules.defer(entry));
    }
}
