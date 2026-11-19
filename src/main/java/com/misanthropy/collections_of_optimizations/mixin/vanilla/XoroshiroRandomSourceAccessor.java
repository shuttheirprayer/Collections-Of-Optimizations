package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(XoroshiroRandomSource.class)
public interface XoroshiroRandomSourceAccessor {
    @Accessor("randomNumberGenerator")
    Xoroshiro128PlusPlus coo$generator();
}
