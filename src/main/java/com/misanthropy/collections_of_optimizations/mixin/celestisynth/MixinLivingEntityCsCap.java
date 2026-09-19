package com.misanthropy.collections_of_optimizations.mixin.celestisynth;

import com.misanthropy.collections_of_optimizations.core.CelestisynthCapHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityCsCap implements CelestisynthCapHolder {

    @Unique
    private Object coo$csCap;

    @Override
    public Object coo$getCsCap() {
        return this.coo$csCap;
    }

    @Override
    public void coo$setCsCap(Object value) {
        this.coo$csCap = value;
    }
}
