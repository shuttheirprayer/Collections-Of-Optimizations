package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlanetDefaultsMemo {

    private static volatile Map<ResourceKey<Level>, Float> gravity = new ConcurrentHashMap<>();

    private static volatile Map<ResourceKey<Level>, Short> temperature = new ConcurrentHashMap<>();

    private PlanetDefaultsMemo() {
    }

    public static Map<ResourceKey<Level>, Float> gravity() {
        return gravity;
    }

    public static Map<ResourceKey<Level>, Short> temperature() {
        return temperature;
    }

    public static void invalidate() {
        gravity = new ConcurrentHashMap<>();
        temperature = new ConcurrentHashMap<>();
    }
}
