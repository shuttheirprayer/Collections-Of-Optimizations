package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtEntityState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityFtState implements FtEntityState {

    @Unique
    private volatile boolean coo$ftStreakUser;

    @Unique
    private volatile boolean coo$ftEffectMarked;

    @Unique
    private boolean coo$ftDevourerListed;

    @Unique
    private int coo$ftScaleStamp = Integer.MIN_VALUE;

    @Unique
    private float coo$ftScale;

    @Override
    public boolean coo$ftStreakUser() {
        return this.coo$ftStreakUser;
    }

    @Override
    public void coo$ftMarkStreakUser() {
        if (!this.coo$ftStreakUser) {
            this.coo$ftStreakUser = true;
        }
    }

    @Override
    public boolean coo$ftEffectMarked() {
        return this.coo$ftEffectMarked;
    }

    @Override
    public void coo$ftMarkEffect() {
        if (!this.coo$ftEffectMarked) {
            this.coo$ftEffectMarked = true;
        }
    }

    @Override
    public boolean coo$ftDevourerListed() {
        return this.coo$ftDevourerListed;
    }

    @Override
    public void coo$ftSetDevourerListed(boolean listed) {
        this.coo$ftDevourerListed = listed;
    }

    @Override
    public int coo$ftScaleStamp() {
        return this.coo$ftScaleStamp;
    }

    @Override
    public float coo$ftScale() {
        return this.coo$ftScale;
    }

    @Override
    public void coo$ftSetScale(float scale, int stamp) {
        this.coo$ftScale = scale;
        this.coo$ftScaleStamp = stamp;
    }
}
