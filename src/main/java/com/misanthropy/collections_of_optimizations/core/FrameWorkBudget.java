package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.util.thread.BlockableEventLoop;

public final class FrameWorkBudget {
    public static volatile long baseNanos = 500_000L;
    public static volatile long perItemNanos = 25_000L;
    public static volatile long maxNanos = 6_000_000L;

    private FrameWorkBudget() {
    }

    public static long budgetNanos(int pending) {
        return Math.min(maxNanos, baseNanos + perItemNanos * pending);
    }

    public static long taskDeadline(BlockableEventLoop<?> loop) {
        if (!CoOConfig.vanillaBudgetFrameTasks) {
            return 0L;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (loop != minecraft || minecraft.level == null || minecraft.screen instanceof ReceivingLevelScreen) {
            return 0L;
        }
        long deadline = System.nanoTime() + budgetNanos(loop.getPendingTasksCount());
        return deadline == 0L ? 1L : deadline;
    }
}
