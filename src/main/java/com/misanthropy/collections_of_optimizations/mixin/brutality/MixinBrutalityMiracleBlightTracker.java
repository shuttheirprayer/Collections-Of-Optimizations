package com.misanthropy.collections_of_optimizations.mixin.brutality;

import com.misanthropy.collections_of_optimizations.core.BrutalityMiracleBlight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.goo.brutality.entity.capabilities.EntityCapabilities$EntityEffectCap", remap = false)
public abstract class MixinBrutalityMiracleBlightTracker {

    @Inject(method = "setMiracleBlighted(Z)V", at = @At("TAIL"), require = 0)
    private void coo$track(boolean blighted, CallbackInfo ci) {
        BrutalityMiracleBlight.set(this, blighted);
    }
}
