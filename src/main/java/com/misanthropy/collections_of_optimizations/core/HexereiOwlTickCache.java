package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.storage.DimensionDataStorage;

import java.lang.ref.WeakReference;

public final class HexereiOwlTickCache {

    private static volatile Entry entry;

    private HexereiOwlTickCache() {
    }

    public static void remember(DimensionDataStorage storage, OwlChunkDataProbe probe) {
        Entry current = entry;
        if (current != null && current.probe == probe && current.storage.get() == storage) {
            return;
        }
        entry = new Entry(new WeakReference<>(storage), probe);
    }

    public static OwlChunkDataProbe find(DimensionDataStorage storage) {
        Entry current = entry;
        if (current != null && current.storage.get() == storage) {
            return current.probe;
        }
        return null;
    }

    private record Entry(WeakReference<DimensionDataStorage> storage, OwlChunkDataProbe probe) {
    }
}
