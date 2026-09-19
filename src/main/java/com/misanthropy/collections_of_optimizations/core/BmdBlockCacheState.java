package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class BmdBlockCacheState {

    public static final ResourceLocation MOB_WARD = new ResourceLocation("bosses_of_mass_destruction", "mob_ward");
    public static final ResourceLocation MONOLITH = new ResourceLocation("bosses_of_mass_destruction", "monolith_block");
    public static final ResourceLocation LEVITATION = new ResourceLocation("bosses_of_mass_destruction", "levitation_block");

    private static final int AUDIT_MASK = 4095;

    private static final Map<ResourceLocation, Set<BlockPos>> TRACKED = new ConcurrentHashMap<>();
    private static final AtomicInteger AUDIT = new AtomicInteger();

    private BmdBlockCacheState() {
    }

    public static void track(Block block, BlockPos pos) {
        if (pos == null) {
            return;
        }
        ResourceLocation key = keyOf(block);
        if (key == null) {
            return;
        }
        TRACKED.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet()).add(pos.immutable());
    }

    public static void trackAll(Block block, List<BlockPos> positions) {
        if (positions == null || positions.isEmpty()) {
            return;
        }
        ResourceLocation key = keyOf(block);
        if (key == null) {
            return;
        }
        Set<BlockPos> known = TRACKED.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet());
        for (int i = 0; i < positions.size(); i++) {
            BlockPos pos = positions.get(i);
            if (pos != null) {
                known.add(pos.immutable());
            }
        }
    }

    public static void untrack(Block block, BlockPos pos) {
        if (pos == null) {
            return;
        }
        ResourceLocation key = keyOf(block);
        if (key == null) {
            return;
        }
        Set<BlockPos> known = TRACKED.get(key);
        if (known != null) {
            known.remove(pos);
        }
    }

    public static boolean absent(ResourceLocation key) {
        Set<BlockPos> known = TRACKED.get(key);
        return known == null || known.isEmpty();
    }

    public static boolean skipScan(ResourceLocation key) {
        if (!absent(key)) {
            return false;
        }
        return (AUDIT.incrementAndGet() & AUDIT_MASK) != 0;
    }

    private static ResourceLocation keyOf(Block block) {
        if (block == null) {
            return null;
        }
        try {
            return ForgeRegistries.BLOCKS.getKey(block);
        } catch (Throwable throwable) {
            return null;
        }
    }
}
