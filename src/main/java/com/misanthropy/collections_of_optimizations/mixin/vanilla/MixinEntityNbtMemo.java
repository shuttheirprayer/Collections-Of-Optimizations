package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.EntityNbtMemo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntityNbtMemo implements EntityNbtMemo {

    @Unique
    private CompoundTag coo$updateFixedTag;

    @Unique
    private CompoundTag coo$noDespawnTimerTag;

    @Override
    public CompoundTag coo$updateFixedTag() {
        return this.coo$updateFixedTag;
    }

    @Override
    public void coo$updateFixedTag(CompoundTag tag) {
        this.coo$updateFixedTag = tag;
    }

    @Override
    public CompoundTag coo$noDespawnTimerTag() {
        return this.coo$noDespawnTimerTag;
    }

    @Override
    public void coo$noDespawnTimerTag(CompoundTag tag) {
        this.coo$noDespawnTimerTag = tag;
    }
}
