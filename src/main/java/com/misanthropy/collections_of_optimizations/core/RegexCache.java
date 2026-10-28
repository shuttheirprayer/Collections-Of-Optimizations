package com.misanthropy.collections_of_optimizations.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class RegexCache {
    private static final Map<String, Pattern> PATTERNS = new ConcurrentHashMap<>();

    private RegexCache() {
    }

    public static boolean matches(String input, String regex) {
        return PATTERNS.computeIfAbsent(regex, Pattern::compile).matcher(input).matches();
    }
}
