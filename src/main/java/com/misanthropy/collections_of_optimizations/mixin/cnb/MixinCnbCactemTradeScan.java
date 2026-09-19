package com.misanthropy.collections_of_optimizations.mixin.cnb;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cgessinger.creaturesandbeasts.entities.CactemEntity$TradeGoal", remap = false)
public abstract class MixinCnbCactemTradeScan {

    @Unique
    private int coo$tradeScanCooldown;

    @Inject(method = "m_8036_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipTradeScan(CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.cnbCactemTradeScanInterval > 1 && this.coo$tradeScanCooldown > 0) {
            this.coo$tradeScanCooldown--;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "m_8036_", at = @At("RETURN"), require = 0)
    private void coo$armTradeScan(CallbackInfoReturnable<Boolean> cir) {
        int interval = CoOConfig.cnbCactemTradeScanInterval;
        if (interval > 1 && !cir.getReturnValueZ()) {
            this.coo$tradeScanCooldown = interval;
        } else {
            this.coo$tradeScanCooldown = 0;
        }
    }
}
