package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlacementFilter.class)
public interface PlacementFilterInvoker {

    @Invoker("shouldPlace")
    boolean coo$shouldPlace(PlacementContext context, RandomSource random, BlockPos pos);
}
