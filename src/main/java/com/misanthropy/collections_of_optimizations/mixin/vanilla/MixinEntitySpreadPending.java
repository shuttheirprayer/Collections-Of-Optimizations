package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.SpreadEntityTicks;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntitySpreadPending implements SpreadEntityTicks.Pending {

    @Unique
    private boolean coo$spreadPending;

    @Override
    public boolean coo$spreadPending() {
        return this.coo$spreadPending;
    }

    @Override
    public void coo$spreadPending(boolean pending) {
        this.coo$spreadPending = pending;
    }
}
