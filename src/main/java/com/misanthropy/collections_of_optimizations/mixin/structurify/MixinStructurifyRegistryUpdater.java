package com.misanthropy.collections_of_optimizations.mixin.structurify;

import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.registry.StructurifyRegistryUpdater;
import com.misanthropy.collections_of_optimizations.core.StructurifyConfigMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StructurifyRegistryUpdater.class, remap = false)
public abstract class MixinStructurifyRegistryUpdater {

    @Inject(
            method = "updateRegistries",
            at = {@At("HEAD"), @At("RETURN")},
            require = 0
    )
    private static void coo$invalidateMirror(UpdateRegistriesEvent event, CallbackInfo ci) {
        StructurifyConfigMirror.invalidate();
    }
}
