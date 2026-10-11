package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeState")
public interface RenderTypeCompositeStateAccessor {

    @Accessor("states")
    ImmutableList<RenderStateShard> coo$states();
}
