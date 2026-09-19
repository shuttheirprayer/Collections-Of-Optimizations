package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

public final class HnnModelHolderCache {

    private static final int MAX_ENTRIES = 1024;

    private static final ConcurrentHashMap<String, Object> CACHE = new ConcurrentHashMap<>();

    private HnnModelHolderCache() {
    }

    public static String keyOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        CompoundTag tag = stack.getTagElement("data_model");
        if (tag == null || !tag.contains("id")) {
            return null;
        }
        String id = tag.getString("id");
        return id.isEmpty() ? null : id;
    }

    public static Object get(String id) {
        return CACHE.get(id);
    }

    public static void put(String id, Object holder) {
        if (holder == null || CACHE.size() >= MAX_ENTRIES) {
            return;
        }
        CACHE.putIfAbsent(id, holder);
    }
}
