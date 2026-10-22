package com.misanthropy.collections_of_optimizations.mixin.pehkui;

import com.misanthropy.collections_of_optimizations.core.ScaleTypeSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import virtuoel.pehkui.api.ScaleType;

@Mixin(value = ScaleType.class, remap = false)
public abstract class MixinScaleTypeSlot implements ScaleTypeSlot {

    @Unique
    private int coo$slot = -1;

    @Override
    public int coo$slot() {
        return this.coo$slot;
    }

    @Override
    public void coo$setSlot(int slot) {
        this.coo$slot = slot;
    }
}
