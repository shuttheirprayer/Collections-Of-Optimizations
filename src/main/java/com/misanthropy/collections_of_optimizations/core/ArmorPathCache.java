package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class ArmorPathCache {

    private static final int LIMIT = 4096;

    private static final Map<Key, String> PATHS = new HashMap<>();

    private static final Key PROBE = new Key();

    private ArmorPathCache() {
    }

    public static String get(Locale locale, String format, Object[] args) {
        if (!RenderSystem.isOnRenderThread()) {
            return null;
        }
        return PATHS.get(PROBE.set(locale, format, args));
    }

    public static String put(Locale locale, String format, Object[] args, String path) {
        if (!RenderSystem.isOnRenderThread()) {
            return path;
        }
        if (PATHS.size() >= LIMIT) {
            PATHS.clear();
        }
        PATHS.put(new Key().set(locale, format, args.clone()), path);
        return path;
    }

    private static final class Key {
        private Locale locale;
        private String format;
        private Object[] args;
        private int hash;

        Key set(Locale locale, String format, Object[] args) {
            this.locale = locale;
            this.format = format;
            this.args = args;
            this.hash = (31 * Objects.hashCode(locale) + format.hashCode()) * 31 + Arrays.hashCode(args);
            return this;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key && hash == key.hash && Objects.equals(locale, key.locale)
                    && format.equals(key.format) && Arrays.equals(args, key.args);
        }
    }
}
