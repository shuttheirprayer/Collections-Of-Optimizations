package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.lwjgl.opengl.GL11C;

public final class FlushBeforeSwap {

    public static boolean atSwap = true;
    public static boolean beforeTick = true;

    private FlushBeforeSwap() {
    }

    public static void swap() {
        if (atSwap && CoOConfig.vanillaFlushBeforeSwap) {
            GL11C.glFlush();
        }
    }

    public static void tick() {
        if (beforeTick && CoOConfig.vanillaFlushBeforeSwap) {
            GL11C.glFlush();
        }
    }
}
