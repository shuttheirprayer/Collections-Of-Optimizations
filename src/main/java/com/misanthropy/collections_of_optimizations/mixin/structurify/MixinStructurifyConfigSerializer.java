package com.misanthropy.collections_of_optimizations.mixin.structurify;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.StructurifyConfigSerializer;
import com.google.gson.JsonObject;
import com.misanthropy.collections_of_optimizations.core.StructurifyConfigMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StructurifyConfigSerializer.class, remap = false)
public abstract class MixinStructurifyConfigSerializer {

    @Inject(
            method = "load",
            at = {@At("HEAD"), @At("RETURN")},
            require = 0
    )
    private static void coo$invalidateMirror(StructurifyConfig config, JsonObject json, CallbackInfo ci) {
        StructurifyConfigMirror.invalidate();
    }
}
