package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CognitionInfectionCache {

    private static final Object MISS = new Object();
    private static final Object LOCK = new Object();

    private static volatile Slot first;
    private static volatile Slot second;

    private CognitionInfectionCache() {
    }

    public static boolean isMiss(Object value) {
        return value == MISS;
    }

    public static Object lookup(RecipeManager manager, Block block) {
        if (manager == null || block == null) {
            return null;
        }
        Map<Block, Object> map = map(manager);
        return map == null ? null : map.get(block);
    }

    public static void store(RecipeManager manager, Block block, BlockState result) {
        if (manager == null || block == null) {
            return;
        }
        Map<Block, Object> map = map(manager);
        if (map != null) {
            map.put(block, result == null ? MISS : result);
        }
    }

    private static Map<Block, Object> map(RecipeManager manager) {
        int generation = RecipeCacheGeneration.generation();
        Slot slot = first;
        if (slot != null && slot.matches(manager, generation)) {
            return slot.map;
        }
        slot = second;
        if (slot != null && slot.matches(manager, generation)) {
            return slot.map;
        }
        synchronized (LOCK) {
            slot = first;
            if (slot != null && slot.matches(manager, generation)) {
                return slot.map;
            }
            slot = second;
            if (slot != null && slot.matches(manager, generation)) {
                return slot.map;
            }
            Slot fresh = new Slot(manager, generation);
            second = first;
            first = fresh;
            return fresh.map;
        }
    }

    private static final class Slot {

        private final WeakReference<RecipeManager> manager;
        private final int generation;
        private final Map<Block, Object> map = new ConcurrentHashMap<>();

        private Slot(RecipeManager manager, int generation) {
            this.manager = new WeakReference<>(manager);
            this.generation = generation;
        }

        private boolean matches(RecipeManager candidate, int generation) {
            return this.generation == generation && this.manager.get() == candidate;
        }
    }
}
