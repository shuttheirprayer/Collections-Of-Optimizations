package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Xoroshiro128PlusPlus.class)
public interface Xoroshiro128PlusPlusAccessor {
    @Accessor("seedLo")
    void coo$setSeedLo(long value);

    @Accessor("seedHi")
    void coo$setSeedHi(long value);
}
