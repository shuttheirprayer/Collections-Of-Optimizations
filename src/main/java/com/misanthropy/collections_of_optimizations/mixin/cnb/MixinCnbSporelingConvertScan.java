package com.misanthropy.collections_of_optimizations.mixin.cnb;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cgessinger.creaturesandbeasts.entities.ai.ConvertItemGoal", remap = false)
public abstract class MixinCnbSporelingConvertScan {

    @Unique
    private int coo$convertScanCooldown;

    @Inject(method = "m_8036_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipConvertScan(CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.cnbSporelingConvertScanInterval > 1 && this.coo$convertScanCooldown > 0) {
            this.coo$convertScanCooldown--;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "m_8036_", at = @At("RETURN"), require = 0)
    private void coo$armConvertScan(CallbackInfoReturnable<Boolean> cir) {
        int interval = CoOConfig.cnbSporelingConvertScanInterval;
        if (interval > 1 && !cir.getReturnValueZ()) {
            this.coo$convertScanCooldown = interval;
        } else {
            this.coo$convertScanCooldown = 0;
        }
    }
}
