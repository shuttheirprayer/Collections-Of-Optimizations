package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FtEntityState;
import net.kayn.fallen_traits.content.item.curio.HandOfCreation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HandOfCreation.class, remap = false)
public abstract class MixinHandOfCreationEffectOwner {

    @Inject(method = "markEffectOwner", at = @At("HEAD"), require = 0)
    private static void coo$markEffectTarget(LivingEntity target, LivingEntity owner, CallbackInfo ci) {
        if (target instanceof FtEntityState state) {
            state.coo$ftMarkEffect();
        }
    }

    @Inject(method = "getEffectOwner", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipNeverMarked(LivingEntity target, long windowTicks, CallbackInfoReturnable<LivingEntity> cir) {
        if (CoOConfig.fallentraitsSkipUnmarkedEffectOwner && target instanceof FtEntityState state && !state.coo$ftEffectMarked()) {
            cir.setReturnValue(null);
        }
    }
}
