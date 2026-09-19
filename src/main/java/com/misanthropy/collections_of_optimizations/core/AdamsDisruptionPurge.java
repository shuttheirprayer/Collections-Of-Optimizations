package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.adamsarsplus.MobEffectInstanceDurationAccessor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class AdamsDisruptionPurge {

    private AdamsDisruptionPurge() {
    }

    public static void expireBeneficial(LivingEntity entity) {
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (instance == null || instance.getDuration() <= 0) {
                continue;
            }
            MobEffect effect = instance.getEffect();
            if (effect == null || !effect.isBeneficial()) {
                continue;
            }
            ((MobEffectInstanceDurationAccessor) (Object) instance).coo$setDuration(0);
        }
    }
}
