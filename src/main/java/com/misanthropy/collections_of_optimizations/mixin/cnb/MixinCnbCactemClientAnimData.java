package com.misanthropy.collections_of_optimizations.mixin.cnb;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.cgessinger.creaturesandbeasts.entities.CactemEntity", remap = false)
public abstract class MixinCnbCactemClientAnimData {

    @Inject(method = "setIdleAnim", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipClientIdleAnim(int anim, CallbackInfo ci) {
        if (CoOConfig.cnbSkipClientAnimDataWrites && ((Entity) (Object) this).level().isClientSide) {
            ci.cancel();
        }
    }

    @Inject(method = "setSpearShown", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipClientSpearShown(boolean shown, CallbackInfo ci) {
        if (CoOConfig.cnbSkipClientAnimDataWrites && ((Entity) (Object) this).level().isClientSide) {
            ci.cancel();
        }
    }
}
