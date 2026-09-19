package com.misanthropy.collections_of_optimizations.mixin.titanium;

import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProgressBarComponent.class, remap = false)
public abstract class MixinTitaniumProgressBar {

    @Shadow
    private int progress;

    @Inject(method = "setProgress", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipRedundantProgressWrite(int value, CallbackInfo ci) {
        if (CoOConfig.titaniumSkipRedundantProgressWrite && value == this.progress) {
            ci.cancel();
        }
    }
}
