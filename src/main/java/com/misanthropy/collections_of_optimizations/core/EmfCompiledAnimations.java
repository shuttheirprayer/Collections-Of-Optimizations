package com.misanthropy.collections_of_optimizations.core;

import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class EmfCompiledAnimations {

    private static final Map<String, Class<?>> CLASSES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, MethodHandle> HANDLES = new ConcurrentHashMap<>();

    private EmfCompiledAnimations() {
    }

    public static String key(byte[] bytes, String className) {
        String internal = className.replace('.', '/');
        byte[] name = internal.getBytes(StandardCharsets.UTF_8);
        String entry = "\u0001" + (char) ((name.length >> 8) & 0xFF) + (char) (name.length & 0xFF) + internal;
        return new String(bytes, StandardCharsets.ISO_8859_1).replace(entry, "\u0001#");
    }

    public static Class<?> define(String key, Supplier<Class<?>> define) {
        Class<?> known = CLASSES.get(key);
        if (known != null) {
            return known;
        }
        Class<?> made = define.get();
        Class<?> raced = CLASSES.putIfAbsent(key, made);
        return raced != null ? raced : made;
    }

    public interface Finder {
        MethodHandle find() throws Throwable;
    }

    public static MethodHandle handle(Class<?> type, Finder finder) throws Throwable {
        MethodHandle known = HANDLES.get(type);
        if (known != null) {
            return known;
        }
        MethodHandle made = finder.find();
        MethodHandle raced = HANDLES.putIfAbsent(type, made);
        return raced != null ? raced : made;
    }
}
