package com.misanthropy.collections_of_optimizations.mixin.cnb;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cgessinger.creaturesandbeasts.entities.CactemEntity$BecomeElderGoal", remap = false)
public abstract class MixinCnbCactemBecomeElderScan {

    @Unique
    private int coo$becomeElderCooldown;

    @Inject(method = "m_8036_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipBecomeElderScan(CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.cnbCactemBecomeElderScanInterval > 1 && this.coo$becomeElderCooldown > 0) {
            this.coo$becomeElderCooldown--;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "m_8036_", at = @At("RETURN"), require = 0)
    private void coo$armBecomeElderScan(CallbackInfoReturnable<Boolean> cir) {
        int interval = CoOConfig.cnbCactemBecomeElderScanInterval;
        if (interval > 1 && !cir.getReturnValueZ()) {
            this.coo$becomeElderCooldown = interval;
        } else {
            this.coo$becomeElderCooldown = 0;
        }
    }
}
