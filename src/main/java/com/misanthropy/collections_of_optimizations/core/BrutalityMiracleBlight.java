package com.misanthropy.collections_of_optimizations.core;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class BrutalityMiracleBlight {

    private static final Set<Object> BLIGHTED = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private BrutalityMiracleBlight() {
    }

    public static void set(Object cap, boolean blighted) {
        if (blighted) {
            BLIGHTED.add(cap);
        } else if (!BLIGHTED.isEmpty()) {
            BLIGHTED.remove(cap);
        }
    }

    public static boolean none() {
        return BLIGHTED.isEmpty();
    }
}
