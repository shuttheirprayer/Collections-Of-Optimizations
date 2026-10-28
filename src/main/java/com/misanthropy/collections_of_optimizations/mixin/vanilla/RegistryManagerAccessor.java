package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.google.common.collect.BiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RegistryManager.class, remap = false)
public interface RegistryManagerAccessor {

    @Accessor("registries")
    BiMap<ResourceLocation, ForgeRegistry<?>> coo$registries();
}
