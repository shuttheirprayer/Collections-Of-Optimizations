package com.misanthropy.collections_of_optimizations.mixin.blockswap;

import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PalettedContainer.class)
public interface PalettedContainerDataAccessor<T> {

    @Accessor("data")
    PalettedContainer.Data<T> coo$data();
}
