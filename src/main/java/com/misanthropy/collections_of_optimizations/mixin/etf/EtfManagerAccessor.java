package com.misanthropy.collections_of_optimizations.mixin.etf;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.texture_handlers.ETFTextureVariator;

import java.util.HashMap;

@Mixin(value = ETFManager.class, remap = false)
public interface EtfManagerAccessor {

    @Accessor("VARIATOR_MAP")
    HashMap<ResourceLocation, ETFTextureVariator> coo$variators();
}
