package com.misanthropy.collections_of_optimizations.core;

public final class RegistrySnapshotReuse {
    private static volatile Thread saving;

    private RegistrySnapshotReuse() {
    }

    public static void beginSave() {
        saving = Thread.currentThread();
    }

    public static void endSave() {
        saving = null;
    }

    public static boolean saving() {
        return saving == Thread.currentThread();
    }
}
