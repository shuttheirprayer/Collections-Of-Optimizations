package com.misanthropy.collections_of_optimizations.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class StaticHardwareReport {

    private static final Set<String> STATIC_GROUPS = Set.of("processor", "graphics", "physical memory");

    private static final Map<String, Map<String, String>> LINES = new ConcurrentHashMap<>();

    private StaticHardwareReport() {
    }

    public static boolean isStatic(String group) {
        return STATIC_GROUPS.contains(group);
    }

    public static Map<String, String> lines(String group) {
        return LINES.get(group);
    }

    public static void remember(String group, Map<String, String> before, Map<String, String> after) {
        Map<String, String> added = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : after.entrySet()) {
            String value = entry.getValue();
            if ("ERR".equals(value)) {
                return;
            }
            if (!value.equals(before.get(entry.getKey()))) {
                added.put(entry.getKey(), value);
            }
        }
        if (!added.isEmpty()) {
            LINES.put(group, added);
        }
    }
}
