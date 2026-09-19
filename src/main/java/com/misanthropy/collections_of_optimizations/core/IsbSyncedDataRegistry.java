package com.misanthropy.collections_of_optimizations.core;

import java.util.Map;

public final class IsbSyncedDataRegistry {

    private static volatile Map<?, ?> lookup;

    private IsbSyncedDataRegistry() {
    }

    public static void capture(Map<?, ?> map) {
        if (map != null && lookup != map) {
            lookup = map;
        }
    }

    public static void purge() {
        Map<?, ?> map = lookup;
        if (map == null || map.isEmpty()) {
            return;
        }
        try {
            map.clear();
        } catch (RuntimeException exception) {
            lookup = null;
        }
    }
}
