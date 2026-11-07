package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.misanthropy.collections_of_optimizations.core.CaveBiomeGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BiomeGenerationConfig.class, remap = false)
public abstract class MixinACBiomeGenerationConfig {

    @Inject(method = "reloadConfig", at = @At("TAIL"), require = 0)
    private static void coo$refreshCaveBiomeGate(CallbackInfo ci) {
        CaveBiomeGate.refresh();
    }
}
