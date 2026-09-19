package com.misanthropy.collections_of_optimizations.mixin.enigmaticdelicacy;

import auviotre.enigmatic.delicacy.registries.EnigmaticDelightEffects;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityDelicacyHooks {

    @Shadow private boolean effectsDirty;
    @Shadow public abstract boolean hasEffect(MobEffect effect);
    @Shadow public abstract Map<MobEffect, MobEffectInstance> getActiveEffectsMap();

    @ModifyReturnValue(method = "hasEffect", at = @At("RETURN"))
    private boolean coo$fadingCountsAsInvisibility(boolean original, MobEffect effect) {
        return original || (effect == MobEffects.INVISIBILITY && getActiveEffectsMap().containsKey(EnigmaticDelightEffects.FADING_EFFECT));
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;tickEffects()V"))
    private void coo$astralDrunkennessDirtiesEffects(CallbackInfo ci) {
        if (this.hasEffect(EnigmaticDelightEffects.ASTRAL_DRUNKENNESS_EFFECT)) {
            this.effectsDirty = true;
        }
    }
}
