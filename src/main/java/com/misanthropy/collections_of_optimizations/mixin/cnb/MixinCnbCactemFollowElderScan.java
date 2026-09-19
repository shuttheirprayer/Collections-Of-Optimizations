package com.misanthropy.collections_of_optimizations.mixin.cnb;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cgessinger.creaturesandbeasts.entities.CactemEntity$FollowElderGoal", remap = false)
public abstract class MixinCnbCactemFollowElderScan {

    @Unique
    private int coo$followElderCooldown;

    @Inject(method = "m_8036_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipFollowElderScan(CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.cnbCactemFollowElderScanInterval > 1 && this.coo$followElderCooldown > 0) {
            this.coo$followElderCooldown--;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "m_8036_", at = @At("RETURN"), require = 0)
    private void coo$armFollowElderScan(CallbackInfoReturnable<Boolean> cir) {
        int interval = CoOConfig.cnbCactemFollowElderScanInterval;
        if (interval > 1 && !cir.getReturnValueZ()) {
            this.coo$followElderCooldown = interval;
        } else {
            this.coo$followElderCooldown = 0;
        }
    }
}
