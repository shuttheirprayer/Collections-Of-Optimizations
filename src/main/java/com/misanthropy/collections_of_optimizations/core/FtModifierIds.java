package com.misanthropy.collections_of_optimizations.core;

import dev.xkmc.l2library.util.math.MathHelper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FtModifierIds {

    private static final Map<String, UUID> IDS = new ConcurrentHashMap<>();

    private FtModifierIds() {
    }

    public static UUID of(String name) {
        UUID id = IDS.get(name);
        if (id == null) {
            id = MathHelper.getUUIDFromString(name);
            IDS.put(name, id);
        }
        return id;
    }
}
