package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClipContext.class)
public interface ClipContextFluidAccessor {

    @Accessor("fluid")
    ClipContext.Fluid coo$fluidMode();
}
