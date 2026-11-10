package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.nbt.CompoundTag;

public interface EntityNbtMemo {

    CompoundTag coo$updateFixedTag();

    void coo$updateFixedTag(CompoundTag tag);

    CompoundTag coo$noDespawnTimerTag();

    void coo$noDespawnTimerTag(CompoundTag tag);
}
