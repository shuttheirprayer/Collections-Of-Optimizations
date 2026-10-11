package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

public final class ConstantLocations {

    private static final ConcurrentHashMap<String, ResourceLocation> BY_PATH = new ConcurrentHashMap<>();

    private ConstantLocations() {
    }

    public static ResourceLocation of(String namespace, String path) {
        ResourceLocation cached = BY_PATH.get(path);
        if (cached != null && cached.getNamespace().equals(namespace)) {
            return cached;
        }
        ResourceLocation created = new ResourceLocation(namespace, path);
        BY_PATH.put(path, created);
        return created;
    }
}
