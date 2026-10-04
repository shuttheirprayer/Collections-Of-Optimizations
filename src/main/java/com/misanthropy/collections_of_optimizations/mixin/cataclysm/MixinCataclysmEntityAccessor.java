package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface MixinCataclysmEntityAccessor {

    @Accessor("dimensions")
    EntityDimensions coo$dimensions();

    @Accessor("random")
    RandomSource coo$random();
}
