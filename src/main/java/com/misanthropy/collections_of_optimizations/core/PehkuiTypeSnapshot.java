package com.misanthropy.collections_of_optimizations.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class PehkuiTypeSnapshot {

    private static volatile ImmutableSet<Object> values = ImmutableSet.of();

    private PehkuiTypeSnapshot() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set values(BiMap map) {
        ImmutableSet<Object> current = values;
        if (current.size() != map.size()) {
            current = ImmutableSet.copyOf(map.values());
            values = current;
        }
        return current;
    }
}
