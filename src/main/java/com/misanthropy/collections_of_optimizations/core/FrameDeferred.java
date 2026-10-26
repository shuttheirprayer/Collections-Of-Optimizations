package com.misanthropy.collections_of_optimizations.core;

import java.util.ArrayList;

public final class FrameDeferred {

    private static final ArrayList<Runnable> TASKS = new ArrayList<>();
    private static boolean ticking;
    private static Object owner;

    private FrameDeferred() {
    }

    public static boolean ticking() {
        return ticking;
    }

    public static void defer(Object level, Runnable task) {
        if (TASKS.isEmpty()) {
            owner = level;
        } else if (owner != level) {
            TASKS.clear();
            owner = level;
        }
        TASKS.add(task);
    }

    public static void begin(Object level) {
        run(level);
        ticking = true;
    }

    public static void end() {
        ticking = false;
    }

    public static void run(Object level) {
        if (TASKS.isEmpty()) {
            return;
        }
        Object queuedFor = owner;
        owner = null;
        if (queuedFor != level || level == null) {
            TASKS.clear();
            return;
        }
        try {
            for (int index = 0; index < TASKS.size(); index++) {
                TASKS.get(index).run();
            }
        } finally {
            TASKS.clear();
        }
    }
}
