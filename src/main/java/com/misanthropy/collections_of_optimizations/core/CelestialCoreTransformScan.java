package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

public final class CelestialCoreTransformScan {

    private static final String RECIPE_CLASS = "com.xiaoyue.celestial_core.content.recipes.TransformationRecipe";

    private static final Object LOCK = new Object();

    private static volatile Snapshot cached;
    private static volatile WeakReference<RecipeManager> cachedManager;
    private static volatile int cachedGeneration = Integer.MIN_VALUE;

    private static Thread armedThread;
    private static Block armedFirst;
    private static Block armedSecond;

    private CelestialCoreTransformScan() {
    }

    public static boolean mayTransform(Level level, ItemEntity item, boolean armSelection) {
        RecipeManager manager = level.getRecipeManager();
        if (manager == null) {
            return true;
        }
        Snapshot snapshot = snapshot(manager);
        if (snapshot == null) {
            return true;
        }
        if (snapshot.stats.length == 0) {
            return false;
        }
        BlockPos on = item.getOnPos();
        Block first = level.getBlockState(on).getBlock();
        Block second = level.getBlockState(on.below()).getBlock();
        for (Block stat : snapshot.stats) {
            if (stat == first || stat == second) {
                if (armSelection) {
                    armedThread = Thread.currentThread();
                    armedFirst = first;
                    armedSecond = second;
                }
                return true;
            }
        }
        return false;
    }

    public static void disarm() {
        if (armedThread == Thread.currentThread()) {
            armedThread = null;
            armedFirst = null;
            armedSecond = null;
        }
    }

    public static boolean rejects(Object recipe) {
        if (armedThread != Thread.currentThread()) {
            return false;
        }
        Snapshot snapshot = cached;
        if (snapshot == null) {
            return false;
        }
        Block stat = snapshot.byRecipe.get(recipe);
        if (stat == null) {
            return false;
        }
        return stat != armedFirst && stat != armedSecond;
    }

    private static Snapshot snapshot(RecipeManager manager) {
        int generation = RecipeCacheGeneration.generation();
        WeakReference<RecipeManager> ref = cachedManager;
        if (ref != null && ref.get() == manager && cachedGeneration == generation) {
            return cached;
        }
        synchronized (LOCK) {
            ref = cachedManager;
            if (ref != null && ref.get() == manager && cachedGeneration == generation) {
                return cached;
            }
            Snapshot snapshot = collect(manager);
            cached = snapshot;
            cachedManager = new WeakReference<>(manager);
            cachedGeneration = generation;
            return snapshot;
        }
    }

    private static Snapshot collect(RecipeManager manager) {
        try {
            Collection<Recipe<?>> recipes = manager.getRecipes();
            if (recipes == null) {
                return null;
            }
            List<Block> stats = new ArrayList<>();
            IdentityHashMap<Object, Block> byRecipe = new IdentityHashMap<>();
            Field field = null;
            for (Recipe<?> recipe : recipes) {
                if (recipe == null || !RECIPE_CLASS.equals(recipe.getClass().getName())) {
                    continue;
                }
                if (field == null) {
                    field = recipe.getClass().getField("stat");
                }
                Object value = field.get(recipe);
                if (value instanceof Block block) {
                    byRecipe.put(recipe, block);
                    if (!stats.contains(block)) {
                        stats.add(block);
                    }
                }
            }
            return new Snapshot(stats.toArray(new Block[0]), byRecipe);
        } catch (Throwable t) {
            return null;
        }
    }

    private static final class Snapshot {

        private final Block[] stats;
        private final IdentityHashMap<Object, Block> byRecipe;

        private Snapshot(Block[] stats, IdentityHashMap<Object, Block> byRecipe) {
            this.stats = stats;
            this.byRecipe = byRecipe;
        }
    }
}
