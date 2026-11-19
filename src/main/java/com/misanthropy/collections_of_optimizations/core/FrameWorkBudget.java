package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.util.thread.BlockableEventLoop;

public final class FrameWorkBudget {
    public static volatile boolean governor = true;
    public static volatile long minNanos = 300_000L;
    public static volatile long capNanos = 2_000_000L;
    public static volatile long hardNanos = 6_000_000L;
    public static volatile long tickFrameNanos = 100_000L;
    public static volatile int sharePercent = 25;
    public static volatile long behindSoftNanos = 30_000_000L;
    public static volatile long behindHardNanos = 80_000_000L;
    public static volatile long behindMaxNanos = 200_000_000L;

    public static volatile long baseNanos = 500_000L;
    public static volatile long perItemNanos = 25_000L;
    public static volatile long maxNanos = 6_000_000L;
    public static volatile long taskNanos = 2_000_000L;
    public static final int CATCH_UP_FRAMES = 4;

    public static long cutFrames;
    public static long hardFrames;

    private static int taskOverruns;
    private static int lightOverruns;
    private static long frameStart;
    private static long frameEma = 2_000_000L;
    private static long spent;
    private static long callStart;
    private static long tasksBehindSince;
    private static long lightBehindSince;
    private static int ticksDue;

    private FrameWorkBudget() {
    }

    public static void frameStart() {
        long now = System.nanoTime();
        long frame = now - frameStart;
        if (frameStart != 0L && frame < 50_000_000L) {
            frameEma += (frame - frameEma) >> 3;
        }
        frameStart = now;
        spent = 0L;
        ticksDue = 0;
    }

    public static void ticksDue(int ticks) {
        ticksDue = ticks;
    }

    private static long allowance(long now, long behindSince) {
        long base = Math.max(minNanos, Math.min(capNanos, frameEma * sharePercent / 100L));
        long behind = behindSince == 0L ? 0L : now - behindSince;
        if (behind > behindMaxNanos) {
            hardFrames++;
            return hardNanos;
        }
        if (behind > behindHardNanos) {
            return Math.min(hardNanos, base * 4L);
        }
        if (behind > behindSoftNanos) {
            return base * 2L;
        }
        return ticksDue > 0 ? Math.min(base, tickFrameNanos) : base;
    }

    public static long budgetNanos(int pending) {
        if (!governor) {
            return Math.min(maxNanos, baseNanos + perItemNanos * pending);
        }
        long now = System.nanoTime();
        callStart = now;
        return Math.max(minNanos / 2L, allowance(now, lightBehindSince) - spent);
    }

    public static boolean isClientLoop(BlockableEventLoop<?> loop) {
        return loop == Minecraft.getInstance();
    }

    public static long taskDeadline(BlockableEventLoop<?> loop) {
        Minecraft minecraft = Minecraft.getInstance();
        if (loop != minecraft) {
            return 0L;
        }
        long now = System.nanoTime();
        callStart = now;
        if (!CoOConfig.vanillaBudgetFrameTasks || minecraft.level == null || minecraft.screen instanceof ReceivingLevelScreen) {
            return 0L;
        }
        long deadline;
        if (governor) {
            deadline = now + Math.max(0L, allowance(now, tasksBehindSince) - spent);
        } else {
            deadline = now + (taskOverruns >= CATCH_UP_FRAMES ? maxNanos : taskNanos);
        }
        return deadline == 0L ? 1L : deadline;
    }

    public static void tasksFinished(boolean overran) {
        taskOverruns = overran ? taskOverruns + 1 : 0;
        if (!governor) {
            return;
        }
        long now = System.nanoTime();
        spent += now - callStart;
        if (!overran) {
            tasksBehindSince = 0L;
        } else {
            cutFrames++;
            if (tasksBehindSince == 0L) {
                tasksBehindSince = now;
            }
        }
    }

    public static boolean lightCatchUp() {
        if (!governor) {
            return lightOverruns >= CATCH_UP_FRAMES;
        }
        return lightBehindSince != 0L && System.nanoTime() - lightBehindSince > behindMaxNanos;
    }

    public static void lightFinished(boolean overran) {
        lightOverruns = overran ? lightOverruns + 1 : 0;
        if (!governor) {
            return;
        }
        long now = System.nanoTime();
        spent += now - callStart;
        if (!overran) {
            lightBehindSince = 0L;
        } else if (lightBehindSince == 0L) {
            lightBehindSince = now;
        }
    }
}
