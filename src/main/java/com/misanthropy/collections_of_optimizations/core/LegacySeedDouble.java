package com.misanthropy.collections_of_optimizations.core;

public final class LegacySeedDouble {

    private LegacySeedDouble() {
    }

    public static double of(long seed) {
        long s = (seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
        s = s * 0x5DEECE66DL + 0xBL & 0xFFFFFFFFFFFFL;
        int high = (int) (s >> 22);
        s = s * 0x5DEECE66DL + 0xBL & 0xFFFFFFFFFFFFL;
        int low = (int) (s >> 21);
        return (double) (((long) high << 27) + (long) low) * 0x1.0p-53;
    }
}
