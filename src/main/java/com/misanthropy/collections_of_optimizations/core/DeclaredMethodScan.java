package com.misanthropy.collections_of_optimizations.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DeclaredMethodScan {

    public static final int UNKNOWN = -1;

    private static final ClassValue<Map<String, List<Class<?>[]>>> DECLARED = new ClassValue<>() {
        @Override
        protected Map<String, List<Class<?>[]>> computeValue(Class<?> type) {
            Map<String, List<Class<?>[]>> byName = new HashMap<>();
            for (Method method : type.getDeclaredMethods()) {
                byName.computeIfAbsent(method.getName(), name -> new ArrayList<>(1)).add(method.getParameterTypes());
            }
            return byName;
        }
    };

    private DeclaredMethodScan() {
    }

    public static int hasOverride(Class<?> clazz, Class<?> superclass, String name, Class<?>[] args) {
        try {
            while (clazz != null && clazz != superclass && superclass.isAssignableFrom(clazz)) {
                List<Class<?>[]> overloads = DECLARED.get(clazz).get(name);
                if (overloads != null) {
                    for (Class<?>[] declared : overloads) {
                        if (Arrays.equals(declared, args)) {
                            return 1;
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
            return 0;
        } catch (Throwable e) {
            return UNKNOWN;
        }
    }
}
