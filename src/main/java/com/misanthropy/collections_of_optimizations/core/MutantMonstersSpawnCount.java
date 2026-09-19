package com.misanthropy.collections_of_optimizations.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

public final class MutantMonstersSpawnCount {

    private static final int SLOTS = 4;

    private static final Object[] LEVELS = new Object[SLOTS];
    private static final Object[] TYPES = new Object[SLOTS];
    private static final int[] STAMPS = new int[SLOTS];
    private static final long[] COUNTS = new long[SLOTS];

    private static int next;

    private MutantMonstersSpawnCount() {
    }

    @SuppressWarnings("rawtypes")
    public static long count(ServerLevel level, EntityType<?> type, Stream stream, Operation<Long> original) {
        MinecraftServer server = level.getServer();
        if (server == null || type == null || !server.isSameThread()) {
            return original.call(stream);
        }
        ResourceKey<Level> key = level.dimension();
        int stamp = server.getTickCount();
        for (int i = 0; i < SLOTS; i++) {
            if (LEVELS[i] == key && TYPES[i] == type) {
                if (STAMPS[i] != stamp) {
                    STAMPS[i] = stamp;
                    COUNTS[i] = original.call(stream);
                }
                long current = COUNTS[i];
                COUNTS[i] = current + 1L;
                return current;
            }
        }
        long counted = original.call(stream);
        int slot = next;
        next = slot + 1 == SLOTS ? 0 : slot + 1;
        LEVELS[slot] = key;
        TYPES[slot] = type;
        STAMPS[slot] = stamp;
        COUNTS[slot] = counted + 1L;
        return counted;
    }
}
