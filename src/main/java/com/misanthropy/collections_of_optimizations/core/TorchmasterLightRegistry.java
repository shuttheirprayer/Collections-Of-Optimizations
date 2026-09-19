package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface TorchmasterLightRegistry {

    boolean coo$tmIsEmpty();

    boolean coo$tmShouldBlockEntity(Entity entity, BlockPos pos);
}
