package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class L2ArcheryUpgradeCache {

    private static final int MAX_ENTRIES = 256;

    private static final ThreadLocal<Map<ListTag, List<Object>>> CACHE =
            ThreadLocal.withInitial(IdentityHashMap::new);

    private L2ArcheryUpgradeCache() {
    }

    public static boolean hasNoUpgradeTag(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        return tag == null || tag.get("upgrades") == null;
    }

    public static ListTag upgradeList(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return null;
        }
        Tag raw = tag.get("upgrades");
        if (!(raw instanceof ListTag list)) {
            return null;
        }
        if (!list.isEmpty() && list.getElementType() != Tag.TAG_STRING) {
            return null;
        }
        return list;
    }

    public static List<Object> get(ListTag key) {
        if (key == null) {
            return null;
        }
        return CACHE.get().get(key);
    }

    public static void put(ListTag key, List<Object> value) {
        if (key == null || value == null) {
            return;
        }
        List<Object> snapshot;
        try {
            snapshot = List.copyOf(value);
        } catch (Throwable ignored) {
            return;
        }
        Map<ListTag, List<Object>> map = CACHE.get();
        if (map.size() >= MAX_ENTRIES) {
            map.clear();
        }
        map.put(key, snapshot);
    }
}
