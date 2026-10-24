package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

public final class ImmersiveBlockEntitySchedule {
    public static final int UNKNOWN = Integer.MIN_VALUE;

    public final Long2IntOpenHashMap priorities = new Long2IntOpenHashMap();
    public long tick = Long.MIN_VALUE;
    public Object levelData;

    public ImmersiveBlockEntitySchedule() {
        priorities.defaultReturnValue(UNKNOWN);
    }

    public interface Holder {
        ImmersiveBlockEntitySchedule coo$immersiveSchedule();
    }
}
