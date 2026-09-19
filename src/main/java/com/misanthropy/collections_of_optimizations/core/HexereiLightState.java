package com.misanthropy.collections_of_optimizations.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public final class HexereiLightState {

    private static final int UNKNOWN = 0;
    private static final int OFF = 1;
    private static final int ON = 2;

    private static final Class<?> MANAGER = resolveManager();
    private static final Set<?> SOURCES = resolveSources();
    private static final Method CLEAR = resolveClear();

    private static volatile int toggleState = UNKNOWN;

    private HexereiLightState() {
    }

    private static Class<?> resolveManager() {
        try {
            return Class.forName("net.joefoxe.hexerei.light.LightManager");
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static Set<?> resolveSources() {
        if (MANAGER == null) {
            return null;
        }
        try {
            Field field = MANAGER.getDeclaredField("dynamicLightSources");
            field.setAccessible(true);
            Object value = field.get(null);
            return value instanceof Set<?> set ? set : null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static Method resolveClear() {
        if (MANAGER == null) {
            return null;
        }
        try {
            Method method = MANAGER.getDeclaredMethod("clearLightSources");
            method.setAccessible(true);
            return method;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static boolean noSources() {
        Set<?> sources = SOURCES;
        return sources != null && sources.isEmpty();
    }

    public static void clearSources() {
        Method method = CLEAR;
        if (method == null || noSources()) {
            return;
        }
        try {
            method.invoke(null);
        } catch (Throwable throwable) {
        }
    }

    public static int cachedToggle() {
        return toggleState;
    }

    public static boolean toggleIsOn(int state) {
        return state == ON;
    }

    public static void rememberToggle(boolean value) {
        toggleState = value ? ON : OFF;
    }

    public static void dropToggle() {
        toggleState = UNKNOWN;
    }
}
