package com.misanthropy.collections_of_optimizations.mixin.embeddium;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ShaderSourceCache;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader", remap = false)
public abstract class MixinShaderLoaderSourceCache {

    @Inject(
            method = "getShaderSource",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$serveCachedSource(ResourceLocation name, CallbackInfoReturnable<String> cir) {
        if (!CoOConfig.embeddiumCacheShaderSources || name == null) {
            return;
        }
        String cached = ShaderSourceCache.get(name);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "getShaderSource",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$storeSource(ResourceLocation name, CallbackInfoReturnable<String> cir) {
        if (CoOConfig.embeddiumCacheShaderSources) {
            ShaderSourceCache.put(name, cir.getReturnValue());
        }
    }
}
