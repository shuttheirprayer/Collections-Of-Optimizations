package com.misanthropy.collections_of_optimizations.mixin.lithostitched;

import dev.worldgen.lithostitched.worldgen.structure.LithostitchedTemplates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LithostitchedTemplates.WeightedEntry.class, remap = false)
public interface LithostitchedWeightedEntryAccessor {

    @Accessor("weight")
    int coo$weight();
}
