package com.misanthropy.collections_of_optimizations.mixin.punchy;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import punchy.client.model.ResourcePackGeoRegistry;

@Mixin(value = ResourcePackGeoRegistry.class, remap = false)
public abstract class MixinPunchyGeoRegistryScan {

    @Inject(method = "loadGeoModels", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipGeoScan(ResourceManager manager, CallbackInfo ci) {
        if (CoOConfig.punchySkipUnusedGeoScan) {
            ci.cancel();
        }
    }

    @Inject(method = "loadModelBones", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipModelScan(ResourceManager manager, CallbackInfo ci) {
        if (CoOConfig.punchySkipUnusedGeoScan) {
            ci.cancel();
        }
    }
}
