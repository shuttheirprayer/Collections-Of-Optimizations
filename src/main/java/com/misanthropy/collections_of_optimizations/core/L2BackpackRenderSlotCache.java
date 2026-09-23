package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public final class L2BackpackRenderSlotCache {

    private static final int MAX_KEYS = 8;
    private static final int MAX_ENTITIES = 512;

    private static final Map<Object, Map<LivingEntity, Optional<ItemStack>>> CACHE = new IdentityHashMap<>();

    private static int stamp = Integer.MIN_VALUE;

    private L2BackpackRenderSlotCache() {
    }

    private static boolean sync() {
        int now = ClientTickStamp.currentOnRenderThread();
        if (now < 0) {
            return false;
        }
        if (now != stamp) {
            stamp = now;
            CACHE.clear();
        }
        return true;
    }

    public static Optional<ItemStack> get(Object key, LivingEntity entity) {
        if (key == null || entity == null || !sync()) {
            return null;
        }
        Map<LivingEntity, Optional<ItemStack>> byEntity = CACHE.get(key);
        if (byEntity == null) {
            return null;
        }
        return byEntity.get(entity);
    }

    public static void put(Object key, LivingEntity entity, Optional<ItemStack> value) {
        if (key == null || entity == null || value == null || !sync()) {
            return;
        }
        Map<LivingEntity, Optional<ItemStack>> byEntity = CACHE.get(key);
        if (byEntity == null) {
            if (CACHE.size() >= MAX_KEYS) {
                return;
            }
            byEntity = new IdentityHashMap<>();
            CACHE.put(key, byEntity);
        }
        if (byEntity.size() < MAX_ENTITIES) {
            byEntity.put(entity, value);
        }
    }
}
