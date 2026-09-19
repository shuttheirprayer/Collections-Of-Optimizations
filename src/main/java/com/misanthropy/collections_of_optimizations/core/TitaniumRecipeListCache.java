package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TitaniumRecipeListCache {

    private static final Map<RecipeType<?>, Object[]> CACHE = new ConcurrentHashMap<>();

    private TitaniumRecipeListCache() {
    }

    public static List<?> get(RecipeType<?> type, Map<ResourceLocation, Recipe<?>> source) {
        Object[] entry = CACHE.get(type);
        if (entry != null && entry[0] == source) {
            return (List<?>) entry[1];
        }
        List<Recipe<?>> copy = List.copyOf(source.values());
        CACHE.put(type, new Object[]{source, copy});
        return copy;
    }
}
