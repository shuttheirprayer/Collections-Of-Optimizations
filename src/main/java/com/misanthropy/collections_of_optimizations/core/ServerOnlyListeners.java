package com.misanthropy.collections_of_optimizations.core;

import java.lang.reflect.Method;
import java.util.Set;

public final class ServerOnlyListeners {

    private static final Set<String> KEYS = Set.of(
    );

    private ServerOnlyListeners() {
    }

    public static boolean contains(Method method) {
        return KEYS.contains(method.getDeclaringClass().getName() + "#" + method.getName());
    }
}
