package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface SmeltingCheckHolder {

    Optional<SmeltingRecipe> coo$cachedSmelting(Container container, Level level);
}
