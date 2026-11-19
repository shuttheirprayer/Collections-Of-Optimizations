package com.misanthropy.collections_of_optimizations.mixin.fwa;

import com.misanthropy.collections_of_optimizations.core.FwaLod;
import fr.madu59.fwa.anims.Animation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Animation.class, remap = false)
public abstract class MixinFwaAnimationLodState implements FwaLod.State {
    @Unique
    private volatile int coo$state;
    @Unique
    private volatile long coo$changed;
    @Unique
    private volatile long coo$requested = -1000L;

    @Override
    public int coo$lodState() {
        return this.coo$state;
    }

    @Override
    public void coo$lodState(int state, long frame) {
        this.coo$state = state;
        this.coo$changed = frame;
    }

    @Override
    public long coo$lodChanged() {
        return this.coo$changed;
    }

    @Override
    public long coo$lodRequested() {
        return this.coo$requested;
    }

    @Override
    public void coo$lodRequested(long frame) {
        this.coo$requested = frame;
    }
}
