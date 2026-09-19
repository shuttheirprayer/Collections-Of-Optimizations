package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BiomancySpatialState {

    private static final ConcurrentHashMap<ResourceKey<Level>, String> LEVEL_KEYS = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Map<?, ?>> SHAPES_BY_LEVEL = new ConcurrentHashMap<>();

    private static volatile Object storage;

    private static volatile Object storageServer;

    private static volatile Object shapesOwner;

    private BiomancySpatialState() {
    }

    public static String levelKey(ServerLevel level) {
        return LEVEL_KEYS.computeIfAbsent(level.dimension(), key -> key.location().toString());
    }

    public static Object cachedStorage(ServerLevel level) {
        Object cached = storage;
        return cached != null && storageServer == level.getServer() ? cached : null;
    }

    public static void putStorage(ServerLevel level, Object value) {
        if (value == null) {
            return;
        }
        Object server = level.getServer();
        if (server == null) {
            return;
        }
        storageServer = server;
        storage = value;
    }

    public static void captureShapes(Object owner, String levelKey, Map<?, ?> shapes) {
        if (owner == null || levelKey == null || shapes == null) {
            return;
        }
        if (shapesOwner != owner) {
            SHAPES_BY_LEVEL.clear();
            shapesOwner = owner;
        }
        SHAPES_BY_LEVEL.put(levelKey, shapes);
    }

    public static void release(Object owner) {
        if (storage == owner) {
            storage = null;
            storageServer = null;
        }
        if (shapesOwner == owner) {
            SHAPES_BY_LEVEL.clear();
            shapesOwner = null;
        }
    }

    public static boolean hasNoShapes(ServerLevel level) {
        Map<?, ?> shapes = SHAPES_BY_LEVEL.get(levelKey(level));
        return shapes != null && shapes.isEmpty();
    }
}
