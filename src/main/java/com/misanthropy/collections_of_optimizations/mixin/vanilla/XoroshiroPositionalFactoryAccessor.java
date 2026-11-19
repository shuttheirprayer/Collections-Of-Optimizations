package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(XoroshiroRandomSource.XoroshiroPositionalRandomFactory.class)
public interface XoroshiroPositionalFactoryAccessor {
    @Accessor("seedLo")
    long coo$seedLo();

    @Accessor("seedHi")
    long coo$seedHi();
}
