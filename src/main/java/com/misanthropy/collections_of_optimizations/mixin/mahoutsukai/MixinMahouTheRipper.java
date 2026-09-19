package com.misanthropy.collections_of_optimizations.mixin.mahoutsukai;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.MahouRipperEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper"}, remap = false)
public abstract class MixinMahouTheRipper {

    @Inject(method = "ripperLivingTick", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanRipperTick(LivingEntity living, CallbackInfo ci) {
        if (!CoOConfig.mahoutsukaiLeanRipperTick) {
            return;
        }
        if (living == null) {
            ci.cancel();
            return;
        }
        MobEffect effect = MahouRipperEffect.invisibility();
        if (effect != null && !living.hasEffect(effect)) {
            ci.cancel();
        }
    }
}
