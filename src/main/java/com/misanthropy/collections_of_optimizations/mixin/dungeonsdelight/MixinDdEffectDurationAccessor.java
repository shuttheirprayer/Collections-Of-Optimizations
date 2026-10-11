package com.misanthropy.collections_of_optimizations.mixin.dungeonsdelight;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MixinDdEffectDurationAccessor {

    @Accessor("duration")
    void coo$ddSetDuration(int duration);
}
