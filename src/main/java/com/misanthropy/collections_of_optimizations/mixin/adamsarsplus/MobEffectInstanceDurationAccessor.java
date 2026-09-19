package com.misanthropy.collections_of_optimizations.mixin.adamsarsplus;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceDurationAccessor {

    @Accessor("duration")
    void coo$setDuration(int duration);
}
