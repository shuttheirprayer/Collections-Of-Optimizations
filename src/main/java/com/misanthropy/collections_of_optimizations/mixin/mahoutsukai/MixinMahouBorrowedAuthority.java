package com.misanthropy.collections_of_optimizations.mixin.mahoutsukai;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"stepsword.mahoutsukai.potion.BorrowedAuthorityPotion"}, remap = false)
public abstract class MixinMahouBorrowedAuthority {

    @Inject(method = "authorityLivingUpdate", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipClientAuthorityTick(LivingEntity entity, CallbackInfo ci) {
        if (CoOConfig.mahoutsukaiSkipClientAuthorityTick
                && entity != null
                && entity.level().isClientSide) {
            ci.cancel();
        }
    }
}
