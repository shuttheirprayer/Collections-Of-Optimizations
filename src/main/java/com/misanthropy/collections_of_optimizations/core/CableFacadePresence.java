package com.misanthropy.collections_of_optimizations.core;

public final class CableFacadePresence {

    private static volatile boolean any;

    private CableFacadePresence() {
    }

    public static boolean any() {
        return any;
    }

    public static void arm() {
        any = true;
    }

    public static void disarm() {
        any = false;
    }
}
