package com.misanthropy.collections_of_optimizations.core;

import com.portingdeadmods.cable_facades.data.CableFacadeSavedData;
import com.portingdeadmods.cable_facades.data.FacadeData;
import com.portingdeadmods.cable_facades.data.helper.ChunkFacadeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.lang.ref.WeakReference;
import java.util.Map;

public final class CableFacadeServerData {

    private static final int LIMIT = 32;
    private static final ThreadLocal<Memo> MEMO = ThreadLocal.withInitial(Memo::new);
    private static volatile Entry[] entries = new Entry[0];
    private static volatile int version;

    private CableFacadeServerData() {
    }

    public static void chunkMapCreated() {
        version++;
    }

    public static FacadeData lookup(ServerLevel level, BlockPos pos) {
        CableFacadeSavedData data = dataFor(level);
        Map<ChunkPos, ChunkFacadeMap> maps = data.getLevelFacadeMap().getChunkFacadeMaps();
        if (maps.isEmpty()) {
            return null;
        }
        Memo memo = MEMO.get();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        int now = version;
        if (memo.data != data || memo.chunkX != chunkX || memo.chunkZ != chunkZ || memo.version != now) {
            memo.map = maps.get(new ChunkPos(chunkX, chunkZ));
            memo.data = data;
            memo.chunkX = chunkX;
            memo.chunkZ = chunkZ;
            memo.version = now;
        }
        ChunkFacadeMap map = memo.map;
        if (map == null) {
            return null;
        }
        Map<BlockPos, FacadeData> chunk = map.getChunkMap();
        return chunk.isEmpty() ? null : chunk.get(pos);
    }

    private static CableFacadeSavedData dataFor(ServerLevel level) {
        Entry[] current = entries;
        for (Entry entry : current) {
            if (entry.level.get() == level) {
                return entry.data;
            }
        }
        CableFacadeSavedData data = CableFacadeSavedData.get(level);
        int keep = 0;
        Entry[] next = new Entry[Math.min(current.length, LIMIT - 1) + 1];
        for (int i = Math.max(0, current.length - (LIMIT - 1)); i < current.length; i++) {
            if (current[i].level.get() != null) {
                next[keep++] = current[i];
            }
        }
        next[keep++] = new Entry(new WeakReference<>(level), data);
        entries = keep == next.length ? next : java.util.Arrays.copyOf(next, keep);
        return data;
    }

    private static final class Memo {
        CableFacadeSavedData data;
        ChunkFacadeMap map;
        int chunkX;
        int chunkZ;
        int version = -1;
    }

    private record Entry(WeakReference<ServerLevel> level, CableFacadeSavedData data) {
    }
}
