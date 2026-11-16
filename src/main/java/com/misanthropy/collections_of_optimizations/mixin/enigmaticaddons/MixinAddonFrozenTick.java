package com.misanthropy.collections_of_optimizations.mixin.enigmaticaddons;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 1500)
public abstract class MixinAddonFrozenTick {

    @TargetHandler(
            mixin = "auviotre.enigmatic.addon.mixin.MixinLivingEntity",
            name = "tickMix"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$skipIdleFrostTick(CallbackInfo ci) {
        if (CoOConfig.enigmaticaddonsSkipIdleFrostScan && ((LivingEntity) (Object) this).getTicksFrozen() == 0) {
            ci.cancel();
        }
    }
}
