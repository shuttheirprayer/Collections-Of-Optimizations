package com.misanthropy.collections_of_optimizations.core;

import net.mehvahdjukaar.moonlight.api.util.math.colors.LABColor;
import net.mehvahdjukaar.moonlight.api.util.math.colors.RGBColor;

import java.util.concurrent.atomic.AtomicLongArray;

public final class ImmersiveWeatheringLeafTint {

    private static final int SLOTS = 1024;
    private static final int BROWN = 8213010;
    private static final LABColor BROWN_LAB = new RGBColor(BROWN).asLAB();
    private static final AtomicLongArray[] CACHE = new AtomicLongArray[11];

    static {
        for (int i = 0; i < CACHE.length; i++) {
            CACHE[i] = new AtomicLongArray(SLOTS);
        }
    }

    private ImmersiveWeatheringLeafTint() {
    }

    public static int faded(int original, int age) {
        if (age < 0 || age >= CACHE.length) {
            return compute(original, age);
        }
        AtomicLongArray cache = CACHE[age];
        int slot = (original * 0x9E3779B9 >>> 22) & (SLOTS - 1);
        long entry = cache.getOpaque(slot);
        if (entry != 0L && (int) (entry >>> 32) == original) {
            return (int) entry;
        }
        int color = compute(original, age);
        cache.setOpaque(slot, (long) original << 32 | color & 0xFFFFFFFFL);
        return color;
    }

    public static int compute(int original, int age) {
        float percentage = (float) age / 10.0f;
        return new RGBColor(original).asLAB().mixWith(BROWN_LAB, percentage).asRGB().toInt();
    }
}
