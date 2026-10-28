package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ForgeRegistry.class, remap = false)
public interface ForgeRegistryAliasAccessor {

    @Accessor("aliases")
    Map<ResourceLocation, ResourceLocation> coo$aliases();
}
