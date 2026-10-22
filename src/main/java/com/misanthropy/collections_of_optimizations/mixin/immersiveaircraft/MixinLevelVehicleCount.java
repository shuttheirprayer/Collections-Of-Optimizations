package com.misanthropy.collections_of_optimizations.mixin.immersiveaircraft;

import com.misanthropy.collections_of_optimizations.core.VehicleCount;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class MixinLevelVehicleCount implements VehicleCount.Holder {

    @Unique
    private volatile int coo$vehicles;

    @Override
    public int coo$vehicles() {
        return this.coo$vehicles;
    }

    @Override
    public synchronized void coo$addVehicles(int delta) {
        this.coo$vehicles += delta;
    }
}
