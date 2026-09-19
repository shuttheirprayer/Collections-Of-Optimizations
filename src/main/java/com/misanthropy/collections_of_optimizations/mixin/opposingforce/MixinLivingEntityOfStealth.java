package com.misanthropy.collections_of_optimizations.mixin.opposingforce;

import com.misanthropy.collections_of_optimizations.core.OpposingForceStealthHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityOfStealth implements OpposingForceStealthHolder {

    @Unique
    private double coo$opposingForceStealth;

    @Unique
    private int coo$opposingForceStealthStamp;

    @Override
    public double coo$opposingForceStealth() {
        return this.coo$opposingForceStealth;
    }

    @Override
    public int coo$opposingForceStealthStamp() {
        return this.coo$opposingForceStealthStamp;
    }

    @Override
    public void coo$storeOpposingForceStealth(double stealth, int stamp) {
        this.coo$opposingForceStealth = stealth;
        this.coo$opposingForceStealthStamp = stamp;
    }
}
