package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class DuplicationlessTrackerSeed {

    private static final String TRACKER_CLASS = "me.kall.duplicationless.data.AbstractEntityTracker";

    private static final Map<Object, Set<ResourceKey<Level>>> QUERIED = new ConcurrentHashMap<>();

    private static volatile boolean broken;
    private static boolean resolved;
    private static Field filtersField;
    private static Method updateMethod;
    private static Method drainMethod;

    private DuplicationlessTrackerSeed() {
    }

    public static boolean skipUpdate(Object tracker, Level level) {
        if (broken || tracker == null || level == null) {
            return false;
        }
        Set<ResourceKey<Level>> queried = QUERIED.get(tracker);
        return queried == null || !queried.contains(level.dimension());
    }

    public static void forget(Object tracker) {
        if (tracker != null) {
            QUERIED.remove(tracker);
        }
    }

    public static void onQuery(Object tracker, Level level) {
        if (broken || tracker == null || level == null) {
            return;
        }
        Set<ResourceKey<Level>> queried = QUERIED.computeIfAbsent(tracker, key -> ConcurrentHashMap.newKeySet());
        if (!queried.add(level.dimension())) {
            return;
        }
        seed(tracker, level);
    }

    private static void seed(Object tracker, Level level) {
        try {
            if (!resolve()) {
                return;
            }
            Iterable<Entity> entities;
            if (level instanceof ServerLevel serverLevel) {
                entities = serverLevel.getAllEntities();
            } else if (level.isClientSide()) {
                entities = DuplicationlessClientEntities.forRendering(level);
            } else {
                return;
            }
            Object rawFilters = filtersField.get(tracker);
            if (!(rawFilters instanceof Map<?, ?> filters)) {
                fail(null);
                return;
            }
            for (Entity entity : entities) {
                if (entity == null || entity.isRemoved()) {
                    continue;
                }
                ObjectList<ResourceLocation> matched = new ObjectArrayList<>();
                for (Map.Entry<?, ?> filter : filters.entrySet()) {
                    if (filter.getKey() instanceof ResourceLocation id && filter.getValue() instanceof Predicate<?> raw) {
                        @SuppressWarnings("unchecked")
                        Predicate<Entity> test = (Predicate<Entity>) raw;
                        if (test.test(entity)) {
                            matched.add(id);
                        }
                    }
                }
                updateMethod.invoke(tracker, entity, level, Boolean.TRUE, matched);
            }
            drainMethod.invoke(tracker);
        } catch (Throwable t) {
            fail(t);
        }
    }

    private static synchronized boolean resolve() throws Exception {
        if (broken) {
            return false;
        }
        if (resolved) {
            return true;
        }
        Class<?> tracker = Class.forName(TRACKER_CLASS, false, DuplicationlessTrackerSeed.class.getClassLoader());
        filtersField = tracker.getDeclaredField("FILTERS");
        filtersField.setAccessible(true);
        updateMethod = tracker.getDeclaredMethod("update", Entity.class, Level.class, boolean.class, ObjectList.class);
        updateMethod.setAccessible(true);
        drainMethod = tracker.getDeclaredMethod("drainUpdateTasks");
        drainMethod.setAccessible(true);
        resolved = true;
        return true;
    }

    private static void fail(Throwable t) {
        broken = true;
        QUERIED.clear();
        LogManager.getLogger("collections_of_optimizations")
                .warn("Could not seed the Duplicationless entity tracker, leaving it on its stock behaviour", t);
    }
}
