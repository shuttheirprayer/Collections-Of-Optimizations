package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

public final class ShaderSourceCache {

    private static final ConcurrentHashMap<ResourceLocation, String> SOURCES = new ConcurrentHashMap<>();

    private ShaderSourceCache() {
    }

    public static String get(ResourceLocation name) {
        return SOURCES.get(name);
    }

    public static void put(ResourceLocation name, String source) {
        if (name != null && source != null) {
            SOURCES.put(name, source);
        }
    }
}
