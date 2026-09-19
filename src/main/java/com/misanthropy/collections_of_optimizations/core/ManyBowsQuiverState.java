package com.misanthropy.collections_of_optimizations.core;

public final class ManyBowsQuiverState {

    private static final String POWER_HOLDER_CLASS = "io.github.apace100.apoli.component.PowerHolderComponent";

    private static final int UNRESOLVED = 0;
    private static final int PRESENT = 1;
    private static final int ABSENT = 2;

    private static volatile int state = UNRESOLVED;

    private ManyBowsQuiverState() {
    }

    public static boolean quiverPossible() {
        int cached = state;
        if (cached == UNRESOLVED) {
            cached = resolve();
            state = cached;
        }
        return cached == PRESENT;
    }

    private static int resolve() {
        try {
            Class.forName(POWER_HOLDER_CLASS, false, ManyBowsQuiverState.class.getClassLoader());
            return PRESENT;
        } catch (Throwable throwable) {
            return ABSENT;
        }
    }
}
