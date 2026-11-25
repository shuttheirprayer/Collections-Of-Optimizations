package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class FeatureBiomes {

    public static final int UNKNOWN = -1;
    public static final int NO = 0;
    public static final int YES = 1;

    private static final int MAX_BIOMES = 64;
    private static final ThreadLocal<FeatureBiomes> LOCAL = ThreadLocal.withInitial(FeatureBiomes::new);

    private final List<Holder<Biome>> biomes = new ArrayList<>();
    private final Reference2ObjectOpenHashMap<PlacedFeature, Allowed> allowed = new Reference2ObjectOpenHashMap<>();
    private final long[][] present = new long[9][];
    private WeakReference<WorldGenRegion> region = new WeakReference<>(null);
    private ChunkGenerator generator;
    private boolean usable;
    private int centerX;
    private int centerZ;
    private int minQuart;
    private int maxQuart;
    private int minSection;

    private FeatureBiomes() {
    }

    public static int decide(PlacementContext context, BlockPos pos) {
        WorldGenLevel level = context.getLevel();
        PlacedFeature feature = context.topFeature().orElse(null);
        if (feature == null || level.getClass() != WorldGenRegion.class) {
            return UNKNOWN;
        }
        FeatureBiomes local = LOCAL.get();
        WorldGenRegion region = (WorldGenRegion) level;
        if (local.region.get() != region || local.generator != context.generator()) {
            local.enter(region, context.generator());
        }
        return local.usable ? local.decide(region, feature, pos) : UNKNOWN;
    }

    private void enter(WorldGenRegion region, ChunkGenerator generator) {
        this.region = new WeakReference<>(region);
        if (this.generator != generator || this.biomes.size() > MAX_BIOMES / 2) {
            this.biomes.clear();
            this.allowed.clear();
        }
        this.generator = generator;
        this.usable = false;
        ChunkPos center = region.getCenter();
        this.centerX = center.x;
        this.centerZ = center.z;
        this.minQuart = QuartPos.fromBlock(region.getMinBuildHeight());
        this.maxQuart = this.minQuart + QuartPos.fromBlock(region.getHeight()) - 1;
        this.minSection = region.getMinSection();
        int sections = region.getSectionsCount();
        for (int i = 0; i < 9; i++) {
            int x = this.centerX + i / 3 - 1;
            int z = this.centerZ + i % 3 - 1;
            if (!region.hasChunk(x, z) || region.getChunk(x, z).getSections().length != sections) {
                return;
            }
            if (this.present[i] == null || this.present[i].length != sections) {
                this.present[i] = new long[sections];
            } else {
                Arrays.fill(this.present[i], 0L);
            }
        }
        this.usable = true;
    }

    private int decide(WorldGenRegion region, PlacedFeature feature, BlockPos pos) {
        int quartX = QuartPos.fromBlock(pos.getX() - 2);
        int quartY = QuartPos.fromBlock(pos.getY() - 2);
        int quartZ = QuartPos.fromBlock(pos.getZ() - 2);
        int x0 = QuartPos.toSection(quartX) - this.centerX + 1;
        int x1 = QuartPos.toSection(quartX + 1) - this.centerX + 1;
        int z0 = QuartPos.toSection(quartZ) - this.centerZ + 1;
        int z1 = QuartPos.toSection(quartZ + 1) - this.centerZ + 1;
        if (x0 < 0 || x1 > 2 || z0 < 0 || z1 > 2) {
            return UNKNOWN;
        }
        int y0 = QuartPos.toSection(Mth.clamp(quartY, this.minQuart, this.maxQuart)) - this.minSection;
        int y1 = QuartPos.toSection(Mth.clamp(quartY + 1, this.minQuart, this.maxQuart)) - this.minSection;
        long corners = 0L;
        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                for (int y = y0; y <= y1; y++) {
                    corners |= this.present(region, x, z, y);
                }
            }
        }
        if (!this.usable) {
            return UNKNOWN;
        }
        long allowedMask = this.allowed(feature);
        if ((corners & ~allowedMask) == 0L) {
            return YES;
        }
        return (corners & allowedMask) == 0L ? NO : UNKNOWN;
    }

    private long present(WorldGenRegion region, int x, int z, int section) {
        long[] masks = this.present[x * 3 + z];
        long mask = masks[section];
        if (mask == 0L) {
            ChunkAccess chunk = region.getChunk(this.centerX + x - 1, this.centerZ + z - 1);
            Collector collector = new Collector();
            chunk.getSection(section).getBiomes().getAll(collector);
            mask = collector.mask;
            masks[section] = mask;
        }
        return mask;
    }

    private long allowed(PlacedFeature feature) {
        Allowed entry = this.allowed.get(feature);
        if (entry == null) {
            entry = new Allowed();
            this.allowed.put(feature, entry);
        }
        for (; entry.known < this.biomes.size(); entry.known++) {
            if (this.generator.getBiomeGenerationSettings(this.biomes.get(entry.known)).hasFeature(feature)) {
                entry.mask |= 1L << entry.known;
            }
        }
        return entry.mask;
    }

    private final class Collector implements Consumer<Holder<Biome>> {
        private long mask;

        @Override
        public void accept(Holder<Biome> biome) {
            int index = FeatureBiomes.this.biomes.indexOf(biome);
            if (index < 0) {
                index = FeatureBiomes.this.biomes.size();
                if (index >= MAX_BIOMES) {
                    FeatureBiomes.this.usable = false;
                    return;
                }
                FeatureBiomes.this.biomes.add(biome);
            }
            this.mask |= 1L << index;
        }
    }

    private static final class Allowed {
        private long mask;
        private int known;
    }
}
