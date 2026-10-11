package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.concurrent.ConcurrentHashMap;

public final class ItemFilterTagCache {

    private static final int MAX_ENTRIES = 4096;

    private static final ConcurrentHashMap<String, ResourceLocation> LOCATIONS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ResourceLocation, TagKey<?>> ITEM_TAGS = new ConcurrentHashMap<>();

    private ItemFilterTagCache() {
    }

    public static ResourceLocation location(String value) {
        ResourceLocation cached = LOCATIONS.get(value);
        if (cached == null) {
            cached = new ResourceLocation(value);
            if (LOCATIONS.size() >= MAX_ENTRIES) {
                LOCATIONS.clear();
            }
            LOCATIONS.put(value, cached);
        }
        return cached;
    }

    @SuppressWarnings("unchecked")
    public static <T> TagKey<T> tag(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
        if ((Object) registry != Registries.ITEM) {
            return TagKey.create(registry, location);
        }
        TagKey<?> cached = ITEM_TAGS.get(location);
        if (cached == null) {
            cached = TagKey.create(registry, location);
            if (ITEM_TAGS.size() >= MAX_ENTRIES) {
                ITEM_TAGS.clear();
            }
            ITEM_TAGS.put(location, cached);
        }
        return (TagKey<T>) cached;
    }
}
