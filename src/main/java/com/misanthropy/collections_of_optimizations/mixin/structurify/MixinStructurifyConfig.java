package com.misanthropy.collections_of_optimizations.mixin.structurify;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.misanthropy.collections_of_optimizations.core.StructurifyConfigMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StructurifyConfig.class, remap = false)
public abstract class MixinStructurifyConfig {

    @Inject(
            method = "save(Z)V",
            at = {@At("HEAD"), @At("RETURN")},
            require = 0
    )
    private void coo$invalidateMirror(boolean syncRegistries, CallbackInfo ci) {
        StructurifyConfigMirror.invalidate();
    }
}
