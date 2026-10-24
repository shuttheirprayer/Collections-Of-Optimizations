package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.Level;

public interface ScheduledTicker {
    long coo$due();

    void coo$due(long due);

    static long nextDue(Level level, long chunkKey, long now) {
        if (!(level instanceof ImmersiveBlockEntitySchedule.Holder holder)) {
            return now + 1L;
        }
        int priority = holder.coo$immersiveSchedule().priorities.get(chunkKey);
        if (priority == ImmersiveBlockEntitySchedule.UNKNOWN || priority <= 1) {
            return now + 1L;
        }
        long next = now + 1L;
        long remainder = Math.floorMod(next + chunkKey, (long) priority);
        return remainder == 0L ? next : next + priority - remainder;
    }
}
